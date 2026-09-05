import { useState } from 'react';
import { theme } from '../theme';
import { loginUser, getMyAccount } from '../services/api';
import { useAuth } from '../context/AuthContext';

export default function LoginForm({ onLoginSuccess, onSwitchToRegister }) {
    const [email, setEmail] = useState('');
    const [password, setPassword] = useState('');
    const [error, setError] = useState('');
    const [loading, setLoading] = useState(false);
    const [focusedField, setFocusedField] = useState('');
    const { login } = useAuth();

    const handleSubmit = async (e) => {
        e.preventDefault();
        setError('');
        setLoading(true);

        try {
            const data = await loginUser({
                email: email.trim(),
                password,
            });

            const token = data.accessToken || data.access_token;

            if (!token) {
                throw new Error('Access token alınamadı.');
            }

            const account = await getMyAccount(token);

            if (!account.walletId) {
                throw new Error('Wallet ID alınamadı.');
            }

            login({
                accessToken: token,
                walletId: account.walletId,
            });

            onLoginSuccess?.();
        } catch (err) {
            setError(err.message || 'Giriş başarısız.');
        } finally {
            setLoading(false);
        }
    };

    return (
        <div style={{ minHeight: '100vh', display: 'flex', alignItems: 'center', justifyContent: 'center', background: theme.page, padding: '1rem' }}>
            <div style={{ width: '100%', maxWidth: 380 }}>
                <div style={{ marginBottom: 32, textAlign: 'center' }}>
                    <div style={{ width: 40, height: 40, borderRadius: 10, background: theme.emerald, margin: '0 auto 16px', display: 'flex', alignItems: 'center', justifyContent: 'center', color: '#fff', fontWeight: 600, fontSize: 15 }}>
                        SW
                    </div>
                    <h1 style={{ fontSize: 20, fontWeight: 500, margin: 0, color: theme.textPrimary }}>Sign in to Smart Wallet</h1>
                    <p style={{ fontSize: 13, color: theme.textMuted, marginTop: 6 }}>Enter your credentials to access your wallet.</p>
                </div>

                <div style={{ background: theme.surface, border: `1px solid ${theme.border}`, borderRadius: theme.radius, padding: '1.75rem', boxShadow: '0 1px 3px rgba(0,0,0,0.04)' }}>
                    <form onSubmit={handleSubmit}>
                        <div style={{ marginBottom: 16 }}>
                            <label style={{ display: 'block', fontSize: 13, fontWeight: 500, color: '#1C1E1A', marginBottom: 6 }}>Email</label>
                            <input
                                type="email"
                                value={email}
                                onChange={(e) => setEmail(e.target.value)}
                                onFocus={() => setFocusedField('email')}
                                onBlur={() => setFocusedField('')}
                                placeholder="name@company.com"
                                style={inputStyle(focusedField === 'email')}
                            />
                        </div>

                        <div style={{ marginBottom: 20 }}>
                            <label style={{ display: 'block', fontSize: 13, fontWeight: 500, color: '#1C1E1A', marginBottom: 6 }}>Password</label>
                            <input
                                type="password"
                                value={password}
                                onChange={(e) => setPassword(e.target.value)}
                                onFocus={() => setFocusedField('password')}
                                onBlur={() => setFocusedField('')}
                                placeholder="••••••••"
                                style={inputStyle(focusedField === 'password')}
                            />
                        </div>

                        {error && (
                            <div style={{ background: theme.dangerSoft, color: theme.danger, fontSize: 13, padding: '10px 12px', borderRadius: 10, marginBottom: 16 }}>
                                {error}
                            </div>
                        )}

                        <button type="submit" disabled={loading} style={buttonStyle(loading)}>
                            {loading ? 'Signing in…' : 'Sign in'}
                        </button>
                    </form>
                </div>

                <p style={{ textAlign: 'center', fontSize: 13, color: theme.textMuted, marginTop: 20 }}>
                    Don't have an account?{' '}
                    <button onClick={onSwitchToRegister} style={{ background: 'none', border: 'none', color: theme.emerald, fontWeight: 500, cursor: 'pointer', padding: 0 }}>
                        Create account
                    </button>
                </p>
            </div>
        </div>
    );
}

function inputStyle(focused) {
    return {
        width: '100%',
        boxSizing: 'border-box',
        padding: '10px 12px',
        fontSize: 14,
        borderRadius: 10,
        border: `1px solid ${focused ? theme.emerald : theme.borderStrong}`,
        background: theme.surface,
        color: theme.textPrimary,
        outline: 'none',
        boxShadow: focused ? `0 0 0 3px ${theme.emeraldSoft}` : 'none',
        transition: 'border-color 0.15s, box-shadow 0.15s',
    };
}

function buttonStyle(disabled) {
    return {
        width: '100%',
        padding: '11px',
        fontSize: 14,
        fontWeight: 500,
        borderRadius: 10,
        border: 'none',
        background: disabled ? theme.borderStrong : theme.emerald,
        color: '#fff',
        cursor: disabled ? 'default' : 'pointer',
    };
}