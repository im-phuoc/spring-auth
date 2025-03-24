'use client'
import React, { useState } from 'react'
import Card from '../common/Card'
import Input from '../common/Input'
import Button from '../common/Button'
import authService from '@/services/auth.service'

const LoginForm = () => {
    const [username, setUsername] = useState('');
    const [password, setPassword] = useState('');

    const handleUsernameChange = (e: React.ChangeEvent<HTMLInputElement>) => {
        setUsername(e.target.value);
    };

    const handlePasswordChange = (e: React.ChangeEvent<HTMLInputElement>) => {
        setPassword(e.target.value);
    };

    const handleLogin = () => {
        authService.login({ username, password }).then((response) => {
            console.log(response);
        }).catch((error) => {
            console.log(error);
        });
    };

  return (
    <Card>
        <h1>Login</h1>
        <Input type="text" placeholder="Username" value={username} onChange={handleUsernameChange} />
        <Input type="password" placeholder="Password" value={password} onChange={handlePasswordChange} />
        <Button onClick={handleLogin}>Login</Button>
    </Card>
  )
}

export default LoginForm