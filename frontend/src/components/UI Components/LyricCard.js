// https://uiverse.io/Tiagoadag/cuddly-catfish-6 --> removed onHover and should have that effect all the time
import React from 'react';
import './LyricCard.css';

const LyricCard = ({ children }) => {
  return (
    <div className="card">
      <div className="card2">
        {children}
      </div>
    </div>
  );
};

export default LyricCard;
