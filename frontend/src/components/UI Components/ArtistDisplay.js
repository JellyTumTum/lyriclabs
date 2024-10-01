// https://uiverse.io/bhaveshxrawat/dangerous-quail-58 --> heavily adjusted but used as template
import React from 'react';
import './ArtistDisplay.css';

const ArtistDisplay = ({ children }) => {
  return (
    <div className="artistDisplay">
      {children}
    </div>
  );
};

export default ArtistDisplay;
