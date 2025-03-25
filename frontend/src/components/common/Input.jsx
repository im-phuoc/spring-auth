import React, { forwardRef } from "react";

const Input = forwardRef(
  (
    {
      label,
      error,
      type = "text",
      className = "",
      wrapperClassName = "",
      ...props
    },
    ref
  ) => {
    const isCheckbox = type === "checkbox";
    const inputClasses = `
      ${isCheckbox 
        ? "h-4 w-4 text-indigo-600 focus:ring-indigo-500 border-gray-300 rounded"
        : "block w-full px-3 py-2 border border-gray-300 rounded-md shadow-sm focus:outline-none focus:ring-indigo-500 focus:border-indigo-500 sm:text-sm"
      }
      ${error ? "border-red-500" : ""}
      ${className}
    `.trim();

    const labelClasses = `
      ${isCheckbox ? "ml-2" : "block text-sm font-medium text-gray-700 mb-1"}
    `.trim();

    const inputElement = (
      <input
        type={type}
        className={inputClasses}
        ref={ref}
        {...props}
      />
    );

    return (
      <div className={`${wrapperClassName} ${isCheckbox ? "flex items-center" : ""}`}>
        {isCheckbox ? (
          <>
            {inputElement}
            {label && (
              <label
                htmlFor={props.id || props.name}
                className={labelClasses}
              >
                {label}
              </label>
            )}
          </>
        ) : (
          <>
            {label && (
              <label
                htmlFor={props.id || props.name}
                className={labelClasses}
              >
                {label}
              </label>
            )}
            {inputElement}
          </>
        )}
        {error && (
          <p className="mt-1 text-sm text-red-600">{error}</p>
        )}
      </div>
    );
  }
);

Input.displayName = "Input";

export default Input;
