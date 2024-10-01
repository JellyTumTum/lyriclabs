// https://uiverse.io/bhaveshxrawat/dangerous-quail-58 --> heavily adjusted but used as template
import React from 'react';
import './TitleCard.css';

const TitleCard = ({ title, style={} }) => {
  return (
    <div className="titleCard" style={style}>
      <h2>{title}</h2>
    </div>
  );
};

export default TitleCard;
