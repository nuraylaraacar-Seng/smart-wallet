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
                            borderRadius: 10,
                            background: t.type === 'success' ? '#0F3D31' : '#4A1B12',
                            color: '#fff',
                            fontSize: 13,
                            boxShadow: '0 4px 16px rgba(0,0,0,0.18)',
                            display: 'flex',
                            alignItems: 'flex-start',
                            gap: 10,
                            animation: 'sw-toast-in 0.2s ease-out',
                        }}
                    >
                        <span style={{ fontSize: 15, lineHeight: 1 }}>{t.type === 'success' ? '✓' : '!'}</span>
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