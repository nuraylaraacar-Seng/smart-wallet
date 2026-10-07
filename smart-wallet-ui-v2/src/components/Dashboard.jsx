import { useState, useEffect, useCallback } from 'react';
import { theme } from '../theme';
import { useAuth } from '../context/AuthContext';
import { getWallet, getTransactions, getMyAccount } from '../services/api';
import Topbar from './Topbar';
import BottomNav from './BottomNav';
import WalletCard from './WalletCard';
import TransferPanel from './TransferPanel';
import TransactionHistory from './TransactionHistory';
import Analytics from './Analytics';
import Profile from './Profile';

const TAB_TITLES = {
    overview: 'Smart Wallet',
    transfer: 'Transfer',
    analytics: 'Analiz',
    history: 'İşlem geçmişi',
    profile: 'Profil',
};

export default function Dashboard({ onLogout }) {
    const { accessToken, walletId } = useAuth();

    const [activeTab, setActiveTab] = useState('overview');
    const [wallet, setWallet] = useState(null);
    const [account, setAccount] = useState(null);
    const [transactions, setTransactions] = useState([]);
    const [loadingWallet, setLoadingWallet] = useState(false);
    const [loadingHistory, setLoadingHistory] = useState(false);
    const [loadingAccount, setLoadingAccount] = useState(false);

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
            setTransactions(Array.isArray(data) ? data : data.content || []);
        } catch {
            setTransactions([]);
        } finally {
            setLoadingHistory(false);
        }
    }, [walletId, accessToken]);

    const loadAccount = useCallback(async () => {
        if (!accessToken) return;
        setLoadingAccount(true);
        try {
            const data = await getMyAccount(accessToken);
            setAccount(data);
        } catch {
            setAccount(null);
        } finally {
            setLoadingAccount(false);
        }
    }, [accessToken]);

    useEffect(() => {
        if (walletId) {
            localStorage.setItem('sw_wallet_id', walletId);
            loadWallet(walletId);
            loadHistory(walletId);
            loadAccount();
        }
    }, [walletId, loadWallet, loadHistory, loadAccount]);

    const handleTransactionComplete = () => {
        loadWallet();
        loadHistory();
    };

    return (
        <div style={{ minHeight: '100vh', display: 'flex', flexDirection: 'column', background: theme.bg }}>
            <Topbar title={TAB_TITLES[activeTab]} />

            <div
                style={{
                    flex: 1,
                    padding: '1.25rem 18px 1.5rem',
                    maxWidth: 480,
                    width: '100%',
                    margin: '0 auto',
                    boxSizing: 'border-box',
                }}
            >
                {activeTab === 'overview' && (
                    <>
                        <WalletCard
                            balance={wallet?.balance ?? wallet?.balanceAmount ?? 0}
                            currency={wallet?.currency ?? wallet?.balanceCurrency ?? 'TRY'}
                            walletId={walletId}
                            loading={loadingWallet && !wallet}
                        />
                        <div style={{ marginTop: 20 }}>
                            <TransactionHistory transactions={transactions.slice(0, 5)} loading={loadingHistory} />
                        </div>
                    </>
                )}

                {activeTab === 'transfer' && (
                    <TransferPanel walletId={walletId} onTransactionComplete={handleTransactionComplete} />
                )}

                {activeTab === 'analytics' && (
                    <Analytics transactions={transactions} loading={loadingHistory} />
                )}

                {activeTab === 'history' && (
                    <TransactionHistory transactions={transactions} loading={loadingHistory} />
                )}

                {activeTab === 'profile' && (
                    <Profile
                        email={account?.email}
                        iban={account?.iban}
                        walletId={walletId}
                        onLogout={onLogout}
                        loading={loadingAccount && !account}
                    />
                )}
            </div>

            <BottomNav active={activeTab} onNavigate={setActiveTab} />
        </div>
    );
}
