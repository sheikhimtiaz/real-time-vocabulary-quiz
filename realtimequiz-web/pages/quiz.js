import { useEffect, useState } from "react";
import { useRouter } from "next/router";
import QuestionPanel from "../components/QuestionPanel";
import Leaderboard from "../components/Leaderboard";

const QUIZ_WS_URL = "ws://localhost:8080/quiz/";

export default function Quiz() {
  const router = useRouter();
  const { quizId, username } = router.query;

  const [ws, setWs] = useState(null);
  const [question, setQuestion] = useState(null);
  const [leaderboard, setLeaderboard] = useState([]);
  const [quizFinished, setQuizFinished] = useState(false);
  const [countdownMessage, setCountdownMessage] = useState(null); 

  useEffect(() => {
    const socket = new WebSocket(QUIZ_WS_URL + quizId);

    socket.onopen = () => {
      console.log( "username -> " + username);
      socket.send(`JOIN ${username}`);
    };

    socket.onmessage = (event) => {
      console.log(event.data);
      const data = event.data;
        
      if (data.includes("Countdown")) {
        setCountdownMessage(data.split(":")[1]);
      } else if (data.includes("Question")) {
        setQuestion(data);
        setCountdownMessage(null);
      } else if (data.includes("Current Ranking")) {
        const scoresStrArr = data.split("#")[1].split("\n").filter(item => item.length > 0);
        console.log(scoresStrArr);
        
        setLeaderboard(scoresStrArr);
      } else if (data.includes("Final Ranking")) {
        setQuizFinished(true);
      }
    };

    socket.onclose = () => {
      console.log("WebSocket closed");
    };

    setWs(socket);

    return () => socket.close();
  }, [username]);

  const submitAnswer = (answer) => {
    if (ws) {
      ws.send(`ANSWER ${answer}`);
      setQuestion(null);
    }
  };

  return (
    <div style={{ textAlign: "center", marginTop: "20px" }}>
      <h1>Welcome to the Quiz</h1>

        <div>
            <div>
                { quizFinished ? (
                    <div>
                    <h2>Quiz Finished!</h2>
                    {/* <Leaderboard data={leaderboard} /> */}
                    </div>
                ) 
                : question ? (
                    <QuestionPanel question={question} onSubmit={submitAnswer} />
                ) 
                : countdownMessage ? (
                    <div>
                    <h2>{countdownMessage}</h2>
                    </div>
                ) : (
                    <div>
                    <h2>Waiting for the next question...</h2>
                    {/* <Leaderboard data={leaderboard} /> */}
                    </div>
                )}
            </div>
            <div>
                <Leaderboard data={leaderboard} />
            </div>
        </div>

    </div>
  );
}
