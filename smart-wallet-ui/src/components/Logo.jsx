import { theme } from '../theme';

export default function Logo({ size = 32 }) {
    return (
        <svg width={size} height={size} viewBox="0 0 48 48" style={{ borderRadius: 10, flexShrink: 0 }}>
            <rect x="4" y="4" width="40" height="40" rx="12" fill={theme.logoBg} />
            <path
                d="M12 30 Q22 30 26 20 Q30 10 38 10"
                fill="none"
                stroke={theme.logoAccent}
                strokeWidth="4"
                strokeLinecap="round"
            />
            <circle cx="26" cy="20" r="3.2" fill={theme.logoAccent} />
        </svg>
    );
}