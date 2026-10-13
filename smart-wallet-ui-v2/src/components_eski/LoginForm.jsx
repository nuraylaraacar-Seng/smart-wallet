import { useState } from 'react';
import { theme } from '../theme';
import { loginUser, getMyAccount } from '../services/api';
import { useAuth } from '../context/AuthContext';
import Logo from './Logo';

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
            const data = await loginUser({ email: email.trim(), password });
            const token = data.accessToken || data.access_token;
            if (!token) throw new Error('Access token alınamadı.');

            const account = await getMyAccount(token);
            if (!account.walletId) throw new Error('Wallet ID alınamadı.');

            login({ accessToken: token, walletId: account.walletId });
            onLoginSuccess?.();
        } catch (err) {
            setError(err.message || 'Giriş başarısız.');
        } finally {
            setLoading(false);
        }
    };

    return (
        <div style={{ minHeight: '100vh', display: 'flex', alignItems: 'center', justifyContent: 'center', background: theme.bg, padding: '1rem' }}>
            <div style={{ width: '100%', maxWidth: 380 }}>
                <div style={{ marginBottom: 32, textAlign: 'center' }}>
                    <div style={{ display: 'flex', justifyContent: 'center', marginBottom: 18 }}>
                        <Logo size={44} />
                    </div>
                    <h1 style={{ fontSize: 21, fontWeight: 600, margin: 0, color: theme.textPrimary, fontFamily: theme.fontDisplay }}>
                        Smart Wallet'a giriş yap
                    </h1>
                    <p style={{ fontSize: 13, color: theme.textMuted, marginTop: 6 }}>Cüzdanına erişmek için bilgilerini gir.</p>
                </div>

                <div style={{ background: theme.surfaceCard, border: `1px solid ${theme.border}`, borderRadius: theme.radius, padding: '1.75rem' }}>
                    <form onSubmit={handleSubmit}>
                        <div style={{ marginBottom: 16 }}>
                            <label style={labelStyle}>E-posta</label>
                            <input
                                type="email"
                                value={email}
                                onChange={(e) => setEmail(e.target.value)}
                                onFocus={() => setFocusedField('email')}
                                onBlur={() => setFocusedField('')}
                                placeholder="ad@sirket.com"
                                style={inputStyle(focusedField === 'email')}
                            />
                        </div>

                        <div style={{ marginBottom: 20 }}>
                            <label style={labelStyle}>Şifre</label>
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
                            {loading ? 'Giriş yapılıyor…' : 'Giriş yap'}
                        </button>
                    </form>
                </div>

                <p style={{ textAlign: 'center', fontSize: 13, color: theme.textMuted, marginTop: 20 }}>
                    Hesabın yok mu?{' '}
                    <button onClick={onSwitchToRegister} style={{ background: 'none', border: 'none', color: theme.mint, fontWeight: 500, cursor: 'pointer', padding: 0 }}>
                        Hesap oluştur
                    </button>
                </p>
            </div>
        </div>
    );
}

const labelStyle = { display: 'block', fontSize: 13, fontWeight: 500, color: '#AEB6A9', marginBottom: 6 };

function inputStyle(focused) {
    return {
        width: '100%',
        boxSizing: 'border-box',
        padding: '11px 14px',
        fontSize: 14,
        borderRadius: 10,
        border: `1px solid ${focused ? theme.mint : theme.border}`,
        background: theme.surfaceRaised,
        color: theme.textPrimary,
        outline: 'none',
        boxShadow: focused ? `0 0 0 3px ${theme.mintSoft}` : 'none',
        transition: 'border-color 0.15s, box-shadow 0.15s',
    };
}

function buttonStyle(disabled) {
    return {
        width: '100%',
        padding: '12px',
        fontSize: 14,
        fontWeight: 600,
        borderRadius: 10,
        border: 'none',
        background: disabled ? theme.borderStrong : theme.mint,
        color: disabled ? theme.textMuted : '#0A0D0A',
        cursor: disabled ? 'default' : 'pointer',
    };
}
