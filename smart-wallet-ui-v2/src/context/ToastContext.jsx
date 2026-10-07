import { createContext, useContext, useState, useCallback } from 'react';

const ToastContext = createContext(null);

export function ToastProvider({ children }) {
    const [toasts, setToasts] = useState([]);

    const showToast = useCallback((message, type = 'success') => {
        const id = crypto.randomUUID();
        setToasts((prev) => [...prev, { id, message, type }]);
        setTimeout(() => {
            setToasts((prev) => prev.filter((t) => t.id !== id));
        }, 4000);
    }, []);

    return (
        <ToastContext.Provider value={{ showToast }}>
            {children}
            <div style={{ position: 'fixed', top: 20, right: 20, zIndex: 1000, display: 'flex', flexDirection: 'column', gap: 10 }}>
                {toasts.map((t) => (
                    <div
                        key={t.id}
                        style={{
                            minWidth: 260,
                            maxWidth: 340,
                            padding: '12px 16px',
                            borderRadius: 12,
                            background: t.type === 'success' ? '#16261F' : '#2A1714',
                            border: `1px solid ${t.type === 'success' ? '#2A9D6F' : '#FF6B5E'}`,
                            color: '#F2F5F0',
                            fontSize: 13,
                            boxShadow: '0 8px 24px rgba(0,0,0,0.4)',
                            display: 'flex',
                            alignItems: 'flex-start',
                            gap: 10,
                            animation: 'sw-toast-in 0.2s ease-out',
                        }}
                    >
                        <span style={{ fontSize: 15, lineHeight: 1, color: t.type === 'success' ? '#3DDC97' : '#FF6B5E' }}>
                            {t.type === 'success' ? '✓' : '!'}
                        </span>
                        <span style={{ lineHeight: 1.4 }}>{t.message}</span>
                    </div>
                ))}
            </div>
            <style>{`
                @keyframes sw-toast-in {
                    from { opacity: 0; transform: translateX(16px); }
                    to { opacity: 1; transform: translateX(0); }
                }
            `}</style>
        </ToastContext.Provider>
    );
}

export function useToast() {
    const ctx = useContext(ToastContext);
    if (!ctx) throw new Error('useToast must be used within ToastProvider');
    return ctx;
}
