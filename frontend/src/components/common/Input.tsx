import React from 'react'

interface InputProps {
    type: string;
    placeholder: string;
    value: string;
    onChange: (e: React.ChangeEvent<HTMLInputElement>) => void;
    className?: string;
}

const Input = ({ type, placeholder, value, onChange, className }: InputProps) => {
  return (
    <input type={type} placeholder={placeholder} value={value} onChange={onChange} className={`border border-gray-300 rounded-md px-4 py-2 ${className}`} />
  )
}

export default Input