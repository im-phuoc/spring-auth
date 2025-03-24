import React from 'react'

interface ButtonProps {
    children: React.ReactNode;
    onClick: () => void;
    className?: string;
}

const Button = ({ children, onClick, className }: ButtonProps) => {
  return (
    <button className={`bg-blue-500 text-white px-4 py-2 rounded-md ${className}`} onClick={onClick}>{children}</button>
  )
}

export default Button