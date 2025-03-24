export interface LoginData {
    username: string;
    password: string;
}

export interface RegisterData extends LoginData {
    email: string;
}

export interface AuthResponse {
    token: string;
    username: string;
    email: string;
    roles: string[];
}

export interface AuthErrorMap {
    [key: string]: string;
}

export interface AuthError {
    message: string;
}
