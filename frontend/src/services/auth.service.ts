import { LoginData, RegisterData } from '@/types/auth.types';
import api from './api';



const authService = {
    login: (data:LoginData) => api.post('/auth/login', data),

    register: (data:RegisterData) => api.post('/auth/register', data),
    
    logout: () => {
        localStorage.removeItem('token');
    }
};

export default authService;