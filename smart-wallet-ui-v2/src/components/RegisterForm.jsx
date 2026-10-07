import { useState } from 'react';
import { theme } from '../theme';
import { registerUser } from '../services/api';
import Logo from './Logo';

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
            setError('E-posta ve şifreni gir.');
            return;
        }
        if (password.length < 8) {
            setError('Şifre en az 8 karakter olmalı.');
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
        <div style={{ minHeight: '100vh', display: 'flex', alignItems: 'center', justifyContent: 'center', background: theme.bg, padding: '1rem' }}>
            <div style={{ width: '100%', maxWidth: 380 }}>
                <div style={{ marginBottom: 32, textAlign: 'center' }}>
                    <div style={{ display: 'flex', justifyContent: 'center', marginBottom: 18 }}>
                        <Logo size={44} />
                    </div>
                    <h1 style={{ fontSize: 21, fontWeight: 600, margin: 0, color: theme.textPrimary, fontFamily: theme.fontDisplay }}>
                        Cüzdanını oluştur
                    </h1>
                    <p style={{ fontSize: 13, color: theme.textMuted, marginTop: 6 }}>Bir dakikadan az sürer.</p>
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

                        <div style={{ marginBottom: 16 }}>
                            <label style={labelStyle}>Şifre</label>
                            <input
                                type="password"
                                value={password}
                                onChange={(e) => setPassword(e.target.value)}
                                onFocus={() => setFocusedField('password')}
                                onBlur={() => setFocusedField('')}
                                placeholder="En az 8 karakter"
                                style={inputStyle(focusedField === 'password')}
                            />
                        </div>

                        <div style={{ marginBottom: 20 }}>
                            <label style={labelStyle}>
                                IBAN <span style={{ color: theme.textMuted, fontWeight: 400 }}>(opsiyonel)</span>
                            </label>
                            <input
                                type="text"
                                value={iban}
                                onChange={(e) => setIban(e.target.value)}
                                onFocus={() => setFocusedField('iban')}
                                onBlur={() => setFocusedField('')}
                                placeholder="TR00 0000 0000 0000 0000 0000 00"
                                style={{ ...inputStyle(focusedField === 'iban'), fontFamily: theme.fontMono, fontSize: 13 }}
                            />
                        </div>

                        {error && (
                            <div style={{ background: theme.dangerSoft, color: theme.danger, fontSize: 13, padding: '10px 12px', borderRadius: 10, marginBottom: 16 }}>
                                {error}
                            </div>
                        )}

                        <button type="submit" disabled={loading} style={buttonStyle(loading)}>
                            {loading ? 'Hesap oluşturuluyor…' : 'Hesap oluştur'}
                        </button>
                    </form>
                </div>

                <p style={{ textAlign: 'center', fontSize: 13, color: theme.textMuted, marginTop: 20 }}>
                    Zaten hesabın var mı?{' '}
                    <button onClick={onSwitchToLogin} style={{ background: 'none', border: 'none', color: theme.mint, fontWeight: 500, cursor: 'pointer', padding: 0 }}>
                        Giriş yap
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
