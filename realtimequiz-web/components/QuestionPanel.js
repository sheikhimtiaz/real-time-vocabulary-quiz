import React, { useState } from "react";

export default function QuestionPanel({ question, options, onSubmit }) {
    const [selectedOption, setSelectedOption] = useState("");
    const [answer, setAnswer] = useState("");
  
    const handleSubmit = () => {
      if (selectedOption) {
        onSubmit(selectedOption);
      }
    };
  
    return (
      <div>
        <h2>{question}</h2>
        <div style={{ marginTop: "20px" }}>
          {options.map((option, index) => (
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
        </div>
        <button onClick={handleSubmit} style={{ marginLeft: "20px", padding: "10px" }}>
          Submit
        </button>
      </div>
    );
  }
  