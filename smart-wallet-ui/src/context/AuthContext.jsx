import { createContext, useContext, useState, useCallback } from 'react';

const AuthContext = createContext(null);

export function AuthProvider({ children }) {
    const [accessToken, setAccessToken] = useState(
        () => localStorage.getItem('accessToken')
    );

    const [walletId, setWalletId] = useState(
        () => localStorage.getItem('sw_wallet_id')
    );

    const login = useCallback(({ accessToken, walletId }) => {
        setAccessToken(accessToken);
        setWalletId(walletId);

        localStorage.setItem('accessToken', accessToken);
        localStorage.setItem('sw_wallet_id', walletId);
    }, []);

    const logout = useCallback(() => {
        setAccessToken(null);
        setWalletId(null);

        localStorage.removeItem('accessToken');
        localStorage.removeItem('sw_wallet_id');
        localStorage.removeItem('access_token');
    }, []);

    return (
        <AuthContext.Provider
            value={{
                accessToken,
                walletId,
                login,
                logout,
            }}
        >
            {children}
        </AuthContext.Provider>
    );
}

export function useAuth() {
    const ctx = useContext(AuthContext);

    if (!ctx) {
        throw new Error('useAuth must be used within AuthProvider');
    }

    return ctx;
}