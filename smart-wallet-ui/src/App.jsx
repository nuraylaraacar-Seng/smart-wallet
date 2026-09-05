import { useState } from 'react';
import { AuthProvider, useAuth } from './context/AuthContext';
import { ToastProvider } from './context/ToastContext';
import LoginForm from './components/LoginForm';
import RegisterForm from './components/RegisterForm';
import Dashboard from './components/Dashboard';

function AppContent() {
    const { accessToken, logout } = useAuth();
    const [showRegister, setShowRegister] = useState(false);

    if (!accessToken) {
        return showRegister ? (
            <RegisterForm
                onRegisterSuccess={() => setShowRegister(false)}
                onSwitchToLogin={() => setShowRegister(false)}
            />
        ) : (
            <LoginForm
                onSwitchToRegister={() => setShowRegister(true)}
            />
        );
    }

    return <Dashboard onLogout={logout} />;
}

export default function App() {
    return (
        <AuthProvider>
            <ToastProvider>
                <AppContent />
            </ToastProvider>
        </AuthProvider>
    );
}