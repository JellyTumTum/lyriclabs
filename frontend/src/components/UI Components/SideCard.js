// https://uiverse.io/Yaseen549/fat-octopus-26
import React from 'react';
import './SideCard.css';

const SideCard = ({ children, style={} }) => {
  return (
    <div style={style} className="sideCard">
      {children}
    </div>
  );
};

export default SideCard;
