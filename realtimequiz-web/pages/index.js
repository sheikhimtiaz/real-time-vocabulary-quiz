import { useState } from "react";
import { useRouter } from "next/router";

export default function Home() {
  const [username, setUsername] = useState("");
  const router = useRouter();

  const handleJoin = (e) => {
    if (username.trim()) {
        // router.push(`/quiz?quizId=quiz123`);
        router.push(`/quiz?id=quiz123&username=${encodeURIComponent(username)}`);
    }
  };

  return (
    <div style={{ textAlign: "center", marginTop: "50px" }}>
      <h1>Join the Quiz</h1>
      <input
        type="text"
        placeholder="Enter your name"
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
