import React from 'react';
import './Button4.css';

const Button4 = ({ text, hoverText, onClick }) => {
  return (
    <button className="glowButton" data-text={hoverText} onClick={onClick}>
      <span className="glowButton-actual-text">&nbsp;{text}&nbsp;</span>
      <span aria-hidden="true" className="glowButton-hover-text">&nbsp;{hoverText}&nbsp;</span>
    </button>
  );
};

export default Button4;
