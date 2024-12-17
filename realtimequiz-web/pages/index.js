import { useState } from "react";
import { useRouter } from "next/router";

export default function Home() {
  const [username, setUsername] = useState("");
  const [quizId, setQuizId] = useState("");
  const router = useRouter();

  const handleJoin = (e) => {
    if (quizId.trim()) {
        // router.push(`/quiz?quizId=quiz123`);
        router.push(`/quiz?quizId=${encodeURIComponent(quizId)}&username=${encodeURIComponent(username)}`);
    }
  };

  return (
    <div style={{ textAlign: "center", marginTop: "50px" }}>
      <h1>Join the Quiz</h1>
      <input
        type="text"
        placeholder="Enter Quiz ID"
        value={quizId}
        onChange={(e) => setQuizId(e.target.value)}
        style={{ padding: "10px", width: "250px" }}
      />
      <input
        type="text"
        placeholder="Enter your username"
        value={username}
        onChange={(e) => setUsername(e.target.value)}
        style={{ padding: "10px", width: "250px" }}
      />
      <button onClick={handleJoin} style={{ marginLeft: "10px", padding: "10px" }}>
        Join Quiz
      </button>
    </div>
  );
}
