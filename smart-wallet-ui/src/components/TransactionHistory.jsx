import { theme } from '../theme';
import { Skeleton } from './Skeleton';

export default function TransactionHistory({ transactions, loading }) {
    return (
        <div style={{
            background: theme.surface,
            border: `1px solid ${theme.border}`,
            borderRadius: theme.radius,
            padding: '1.5rem',
        }}>
            <p style={{ fontSize: 14, fontWeight: 500, margin: '0 0 4px', color: theme.textPrimary }}>
                Recent activity
            </p>
            <p style={{ fontSize: 12.5, color: theme.textMuted, margin: '0 0 18px' }}>
                Your latest transfers, deposits, and withdrawals.
            </p>

            {loading ? (
                <div style={{ display: 'flex', flexDirection: 'column', gap: 14 }}>
                    {[1, 2, 3].map((i) => (
                        <div key={i} style={{ display: 'flex', alignItems: 'center', gap: 12 }}>
                            <Skeleton width={36} height={36} radius={10} />
                            <div style={{ flex: 1 }}>
                                <Skeleton width="60%" height={12} style={{ marginBottom: 6 }} />
                                <Skeleton width="35%" height={10} />
                            </div>
                            <Skeleton width={70} height={14} />
                        </div>
                    ))}
                </div>
            ) : transactions.length === 0 ? (
                <div style={{ textAlign: 'center', padding: '2.5rem 0', color: theme.textMuted, fontSize: 13 }}>
                    No transactions yet.
                </div>
            ) : (
                <div style={{ display: 'flex', flexDirection: 'column' }}>
                    {transactions.map((tx, i) => (
                        <TransactionRow key={tx.id} tx={tx} isLast={i === transactions.length - 1} />
                    ))}
                </div>
            )}
        </div>
    );
}

function TransactionRow({ tx, isLast }) {
    const isIncoming = tx.type === 'DEPOSIT' || tx.direction === 'IN';
    const sign = isIncoming ? '+' : '\u2212';
    const color = isIncoming ? theme.success : theme.danger;
    const bg = isIncoming ? theme.successSoft : theme.dangerSoft;

    return (
        <div style={{
            display: 'flex',
            alignItems: 'center',
            gap: 12,
            padding: '12px 0',
            borderBottom: isLast ? 'none' : `1px solid ${theme.border}`,
        }}>
            <div style={{
                width: 36, height: 36, borderRadius: 10,
                background: bg, color,
                display: 'flex', alignItems: 'center', justifyContent: 'center',
                fontSize: 16, fontWeight: 600, flexShrink: 0,
            }}>
                {isIncoming ? '\u2193' : '\u2191'}
            </div>
            <div style={{ flex: 1, minWidth: 0 }}>
                <p style={{ fontSize: 13.5, fontWeight: 500, margin: 0, color: theme.textPrimary }}>
                    {formatType(tx.type)}
                </p>
                <p style={{ fontSize: 12, color: theme.textMuted, margin: '2px 0 0' }}>
                    {formatDate(tx.createdAt)}
                </p>
            </div>
            <span style={{ fontSize: 14, fontWeight: 500, color, whiteSpace: 'nowrap' }}>
                {sign}{formatAmount(tx.amount)} {tx.currency}
            </span>
        </div>
    );
}

function formatType(type) {
    const map = { TRANSFER: 'Transfer', DEPOSIT: 'Deposit', WITHDRAW: 'Withdrawal' };
    return map[type] || type;
}

function formatAmount(value) {
    return new Intl.NumberFormat('en-US', { minimumFractionDigits: 2, maximumFractionDigits: 2 }).format(value);
}

function formatDate(iso) {
    if (!iso) return '';
    return new Date(iso).toLocaleString('en-US', { month: 'short', day: 'numeric', hour: '2-digit', minute: '2-digit' });
}