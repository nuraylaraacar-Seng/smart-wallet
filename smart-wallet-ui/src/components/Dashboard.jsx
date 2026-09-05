import { useState, useEffect, useCallback } from 'react';
import { theme } from '../theme';
import { useAuth } from '../context/AuthContext';
import { getWallet, getTransactions } from '../services/api';
import Sidebar from './Sidebar';
import Topbar from './Topbar';
import WalletCard from './WalletCard';
import TransferPanel from './TransferPanel';
import TransactionHistory from './TransactionHistory';

export default function Dashboard({ onLogout }) {
    const { accessToken, walletId } = useAuth();

    const [activeTab, setActiveTab] = useState('overview');
    const [wallet, setWallet] = useState(null);
    const [transactions, setTransactions] = useState([]);
    const [loadingWallet, setLoadingWallet] = useState(false);
    const [loadingHistory, setLoadingHistory] = useState(false);

    const loadWallet = useCallback(async (idToUse) => {
        const id = idToUse || walletId;

        if (!id || !accessToken) return;

        setLoadingWallet(true);

        try {
            const data = await getWallet(id, accessToken);
            setWallet(data);
        } catch {
            setWallet(null);
        } finally {
            setLoadingWallet(false);
        }
    }, [walletId, accessToken]);

    const loadHistory = useCallback(async (idToUse) => {
        const id = idToUse || walletId;

        if (!id || !accessToken) return;

        setLoadingHistory(true);

        try {
            const data = await getTransactions(id, accessToken);
            setTransactions(
                Array.isArray(data)
                    ? data
                    : data.content || []
            );
        } catch {
            setTransactions([]);
        } finally {
            setLoadingHistory(false);
        }
    }, [walletId, accessToken]);

    useEffect(() => {
        if (walletId) {
            localStorage.setItem('sw_wallet_id', walletId);
            loadWallet(walletId);
            loadHistory(walletId);
        }
    }, [walletId, loadWallet, loadHistory]);

    const handleTransactionComplete = () => {
        loadWallet();
        loadHistory();
    };

    return (
        <div
            style={{
                display: 'flex',
                minHeight: '100vh',
                background: theme.page,
            }}
        >
            <Sidebar
                active={activeTab}
                onNavigate={setActiveTab}
            />

            <div
                style={{
                    flex: 1,
                    display: 'flex',
                    flexDirection: 'column',
                }}
            >
                <Topbar
                    email={wallet?.userEmail}
                    onLogout={onLogout}
                />

                <div
                    style={{
                        flex: 1,
                        padding: '2.5rem 2rem',
                        maxWidth: 720,
                        width: '100%',
                        margin: '0 auto',
                        boxSizing: 'border-box',
                    }}
                >
                    {activeTab === 'overview' && (
                        <>
                            <WalletCard
                                balance={
                                    wallet?.balance ??
                                    wallet?.balanceAmount ??
                                    0
                                }
                                currency={
                                    wallet?.currency ??
                                    wallet?.balanceCurrency ??
                                    'TRY'
                                }
                                walletId={walletId}
                                loading={loadingWallet && !wallet}
                            />

                            <div style={{ marginTop: 24 }}>
                                <TransactionHistory
                                    transactions={transactions}
                                    loading={loadingHistory}
                                />
                            </div>
                        </>
                    )}

                    {activeTab === 'transfer' && (
                        <TransferPanel
                            walletId={walletId}
                            onTransactionComplete={handleTransactionComplete}
                        />
                    )}

                    {activeTab === 'history' && (
                        <TransactionHistory
                            transactions={transactions}
                            loading={loadingHistory}
                        />
                    )}
                </div>
            </div>
        </div>
    );
}

