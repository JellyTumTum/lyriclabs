// https://uiverse.io/TISEPSE/ugly-badger-82
import React from 'react';
import './Button1.css';

const Button1 = ({ label, children, onClick }) => {
    return (
        <button onClick={onClick}>
            <a class="btn2"><span class="spn2">{label}</span>
            {children}</a>
        </button>
    );
};

export default Button1;
