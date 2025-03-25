import React, { useState, useEffect } from "react";
import { useForm } from "react-hook-form";
import { useNavigate } from "react-router-dom";
import toast from "react-hot-toast";
import Card from "../common/Card";
import Input from "../common/Input";
import Button from "../common/Button";
import { useAuth } from "../../contexts/AuthProvider";
import MainLayout from "../layouts/MainLayout";

const Login = () => {
  const navigate = useNavigate();
  const { login } = useAuth();
  const [isSubmitting, setIsSubmitting] = useState(false);
  const [hasError, setHasError] = useState(false);

  const {
    register,
    formState: { errors, isDirty },
    handleSubmit,
    setError,
    clearErrors,
    reset,
    watch,
  } = useForm({
    mode: "onBlur",
    defaultValues: {
      username: "",
      password: "",
    },
  });

  // Auto-reset form errors when user starts typing again after an error
  const formValues = watch();

  useEffect(() => {
    if (hasError && isDirty) {
      clearErrors();
      setHasError(false);
    }
  }, [formValues, hasError, isDirty, clearErrors]);

  const onSubmit = async (data) => {
    if (isSubmitting) return;

    try {
      setIsSubmitting(true);
      clearErrors();

      const result = await login(data.username, data.password);

      if (result.success) {
        toast.success("Login successful");
        reset();
        navigate("/dashboard");
      } else {
        setHasError(true);
        toast.error(result.message);
      }
    } catch (error) {
      console.error("Login error:", error);
      toast.error(error.response?.data?.data?.message || "An unexpected error occurred");
      setHasError(true);
    } finally {
      setIsSubmitting(false);
    }
  };

  return (
    <div className="min-h-screen flex items-center justify-center p-4">
      <Card className="w-full max-w-sm">
        <h1 className="text-2xl font-semibold text-gray-900 mb-6">Sign in</h1>

        <form
          onSubmit={handleSubmit(onSubmit)}
          className="space-y-4"
          noValidate
        >
          <Input
            label="Username"
            type="text"
            disabled={isSubmitting}
            {...register("username", {
              required: "Username is required",
              minLength: {
                value: 2,
                message: "Username must be between 2 and 20 characters",
              },
              maxLength: {
                value: 20,
                message: "Username must be between 2 and 20 characters",
              },
            })}
            error={errors.username?.message}
            className={errors.username ? "border-red-500" : ""}
          />

          <Input
            label="Password"
            type="password"
            disabled={isSubmitting}
            {...register("password", {
              required: "Password is required",
              minLength: {
                value: 6,
                message: "Password must be at least 6 characters",
              },
            })}
            error={errors.password?.message}
            className={errors.password ? "border-red-500" : ""}
          />

          <Button
            type="submit"
            loading={isSubmitting}
            disabled={isSubmitting}
            className="w-full"
          >
            {isSubmitting ? "Signing in..." : "Sign in"}
          </Button>
        </form>
      </Card>
    </div>
  );
};

export default Login;
