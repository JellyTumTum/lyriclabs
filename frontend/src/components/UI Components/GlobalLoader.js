import React from 'react';
import './GlobalLoader.css';
// https://uiverse.io/fateself/tricky-crab-38

const GlobalLoader = () => {
    return (
        <div className="loader-container">
            <div className="loader">
                {/* have to create dynamically cause react doesnt like it otherwise */}
                {Array.from({ length: 20 }, (_, i) => (
                    <span key={i} style={{ '--i': i + 1 }}></span>
                ))}
            </div>
        </div>
    );
};

export default GlobalLoader;
