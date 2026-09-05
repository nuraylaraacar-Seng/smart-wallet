import { useState } from 'react';
import { theme } from '../theme';
import { registerUser } from '../services/api';

export default function RegisterForm({ onRegisterSuccess, onSwitchToLogin }) {
    const [email, setEmail] = useState('');
    const [password, setPassword] = useState('');
    const [iban, setIban] = useState('');
    const [error, setError] = useState('');
    const [loading, setLoading] = useState(false);
    const [focusedField, setFocusedField] = useState('');

    const handleSubmit = async (e) => {
        e.preventDefault();
        setError('');

        if (!email.trim() || !password) {
            setError('Enter your email and password.');
            return;
        }
        if (password.length < 8) {
            setError('Password must be at least 8 characters.');
            return;
        }

        setLoading(true);
        try {
            await registerUser({ email, password, iban: iban.trim() || null });
            onRegisterSuccess();
        } catch (err) {
            setError(err.message);
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
                    <h1 style={{ fontSize: 20, fontWeight: 500, margin: 0, color: theme.textPrimary }}>Create your wallet</h1>
                    <p style={{ fontSize: 13, color: theme.textMuted, marginTop: 6 }}>Takes less than a minute.</p>
                </div>

                <div style={{ background: theme.surface, border: `1px solid ${theme.border}`, borderRadius: theme.radius, padding: '1.75rem', boxShadow: '0 1px 3px rgba(0,0,0,0.04)' }}>
                    <form onSubmit={handleSubmit}>
                        <div style={{ marginBottom: 16 }}>
                            <label style={labelStyle}>Email</label>
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

                        <div style={{ marginBottom: 16 }}>
                            <label style={labelStyle}>Password</label>
                            <input
                                type="password"
                                value={password}
                                onChange={(e) => setPassword(e.target.value)}
                                onFocus={() => setFocusedField('password')}
                                onBlur={() => setFocusedField('')}
                                placeholder="At least 8 characters"
                                style={inputStyle(focusedField === 'password')}
                            />
                        </div>

                        <div style={{ marginBottom: 20 }}>
                            <label style={labelStyle}>
                                IBAN <span style={{ color: theme.textMuted, fontWeight: 400 }}>(optional)</span>
                            </label>
                            <input
                                type="text"
                                value={iban}
                                onChange={(e) => setIban(e.target.value)}
                                onFocus={() => setFocusedField('iban')}
                                onBlur={() => setFocusedField('')}
                                placeholder="TR00 0000 0000 0000 0000 0000 00"
                                style={{ ...inputStyle(focusedField === 'iban'), fontFamily: 'IBM Plex Mono, monospace', fontSize: 13 }}
                            />
                        </div>

                        {error && (
                            <div style={{ background: theme.dangerSoft, color: theme.danger, fontSize: 13, padding: '10px 12px', borderRadius: 10, marginBottom: 16 }}>
                                {error}
                            </div>
                        )}

                        <button type="submit" disabled={loading} style={buttonStyle(loading)}>
                            {loading ? 'Creating account\u2026' : 'Create account'}
                        </button>
                    </form>
                </div>

                <p style={{ textAlign: 'center', fontSize: 13, color: theme.textMuted, marginTop: 20 }}>
                    Already have an account?{' '}
                    <button onClick={onSwitchToLogin} style={{ background: 'none', border: 'none', color: theme.emerald, fontWeight: 500, cursor: 'pointer', padding: 0 }}>
                        Sign in
                    </button>
                </p>
            </div>
        </div>
    );
}

const labelStyle = { display: 'block', fontSize: 13, fontWeight: 500, color: '#1C1E1A', marginBottom: 6 };

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