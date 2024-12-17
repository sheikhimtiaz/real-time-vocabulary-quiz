import { useEffect, useState } from "react";
import { useRouter } from "next/router";
import QuestionPanel from "../components/QuestionPanel";
import Leaderboard from "../components/Leaderboard";

const QUIZ_WS_URL = "ws://localhost:8080/quiz/quiz123";

export default function Quiz() {
  const router = useRouter();
  const { username } = router.query;

  const [ws, setWs] = useState(null);
  const [question, setQuestion] = useState(null);
  const [leaderboard, setLeaderboard] = useState([]);
  const [quizFinished, setQuizFinished] = useState(false);

  useEffect(() => {
    const socket = new WebSocket(QUIZ_WS_URL);

    socket.onopen = () => {
        console.log( "username -> " + username);
        
      socket.send(`JOIN ${username}`);
    };

    socket.onmessage = (event) => {
      console.log(event.data);
      const data = event.data;
        
      if (data.includes("Question")) {
        const temp = data.split(":");
        console.log(temp);
        
        setQuestion(data);
      } else if (data.includes("CurrentRanking")) {
        setLeaderboard(data);
      } else if (data.includes("FinalRanking")) {
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

      {quizFinished ? (
        <div>
          <h2>Quiz Finished!</h2>
          <Leaderboard data={leaderboard} />
        </div>
      ) : question ? (
        <QuestionPanel question={question} onSubmit={submitAnswer} />
      ) : (
        <div>
          <h2>Waiting for the next question...</h2>
          <Leaderboard data={leaderboard} />
        </div>
      )}
    </div>
  );
}
