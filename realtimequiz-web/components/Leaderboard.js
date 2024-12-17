export default function Leaderboard({ data }) {
  return (
    <div>
      <h3>Leaderboard</h3>
      <ul>
        {data.map((entry, index) => (
          <li key={index}>
            {entry} 
          </li>
        ))}
      </ul>
    </div>
  );
}
