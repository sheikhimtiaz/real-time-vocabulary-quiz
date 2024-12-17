import React, { useState } from "react";

export default function QuestionPanel({ question, onSubmit }) {
    // const [selectedOption, setSelectedOption] = useState("");
    const [answer, setAnswer] = useState("");
  
    const handleSubmit = () => {
      if (answer) {
        onSubmit(answer);
      }
    };
  
    return (
      <div>
        <h2>{question}</h2>
        <input
            type="text"
            placeholder="Enter your answer"
            value={answer}
            onChange={(e) => setAnswer(e.target.value)}
            style={{ padding: "10px", width: "250px" }}
        />
        <button onClick={handleSubmit} style={{ marginLeft: "20px", padding: "10px" }}>
          Submit
        </button>
        {/* <div style={{ marginTop: "20px" }}>
          {question.options.map((option, index) => (
            <div key={index}>
              <label>
                <input
                  type="radio"
                  name="option"
                  value={option}
                  onChange={() => setSelectedOption(option)}
                />
                {option}
              </label>
            </div>
          ))}
        </div> */}
      </div>
    );
  }
  