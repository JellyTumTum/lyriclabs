// https://uiverse.io/LightAndy1/empty-catfish-94
import React from 'react';
import './CustomCard.css';

const CustomCard = ({ title, children }) => {
    return (
      <div className="package">
        <div className="package2">
          <p className="text">{title}</p>
          {children}
        </div>
      </div>
    );
  };
  
  export default CustomCard;
