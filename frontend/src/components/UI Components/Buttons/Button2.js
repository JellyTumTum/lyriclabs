// https://uiverse.io/nikk7007/blue-mayfly-40
import React from 'react';
import './Button2.css'; 

const Button2 = ({ children, onClick, disabled }) => {
  return (
    <button onClick={ onClick } disabled={disabled} className="button">
      {children}
    </button>
  );
};

export default Button2;
