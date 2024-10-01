// https://uiverse.io/shah1345/spicy-liger-32
import React from 'react';
import './Button3.css'; 

const Button3 = ({ children, onClick, className }) => {
  return (
    <button onClick={onClick} className="button3">
      {children}
    </button>
  );
};

export default Button3;
