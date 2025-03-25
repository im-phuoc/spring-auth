import React from "react";

const Card = ({ children, className = "" }) => {
  return (
    <div className={`bg-white rounded-xl shadow-lg p-8 ${className}`}>
      {children}
    </div>
  );
};

export default Card;
