import React from 'react'

interface CardProps {
    children: React.ReactNode;
    className?: string;
}

const Card = ({ children, className }: CardProps) => {
  return (
    <div className={`bg-white shadow-md rounded-md p-4 ${className}`}>{children}</div>
  )
}

export default Card