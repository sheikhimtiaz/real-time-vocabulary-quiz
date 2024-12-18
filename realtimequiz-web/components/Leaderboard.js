export default function Leaderboard({ data }) {
  return (
    <div style={{paddingTop:"50px"}}>
      <h3 style={{border:"1px solid black", textWrap:"wrap"}}>Leaderboard</h3>
      <div>
        {data.map((entry, index) => (
          <div key={index} style={{paddingBottom:"10px"}}>
            {entry} points
          </div>
        ))}
      </div>
    </div>
  );
}
