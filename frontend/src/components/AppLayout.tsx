import React, { ReactNode } from 'react';
import { Layout, Menu, Dropdown, Button, Avatar, Space } from 'antd';
import { UserOutlined, LogoutOutlined, HomeOutlined, TeamOutlined, ApartmentOutlined } from '@ant-design/icons';
import { useNavigate, useLocation } from 'react-router-dom';
import { useAuthStore } from '../store/auth';

const { Header, Content, Footer } = Layout;

interface AppLayoutProps {
  children: ReactNode;
}

const AppLayout: React.FC<AppLayoutProps> = ({ children }) => {
  const navigate = useNavigate();
  const location = useLocation();
  const { user, logout, isAuthenticated } = useAuthStore();

  const handleLogout = () => {
    logout();
    navigate('/login');
  };

  const menuItems = [
    {
      key: '/',
      icon: <HomeOutlined />,
      label: '首页',
      onClick: () => navigate('/'),
    },
    {
      key: '/users',
      icon: <TeamOutlined />,
      label: '用户管理',
      onClick: () => navigate('/users'),
    },
    {
      key: '/departments',
      icon: <ApartmentOutlined />,
      label: '部门管理',
      onClick: () => navigate('/departments'),
    },
  ];

  const userMenuItems = [
    {
      key: 'logout',
      icon: <LogoutOutlined />,
      label: '退出登录',
      onClick: handleLogout,
    },
  ];

  if (!isAuthenticated) {
    return <>{children}</>;
  }

  return (
    <Layout style={{ minHeight: '100vh' }}>
      <Header style={{ display: 'flex', alignItems: 'center', justifyContent: 'space-between' }}>
        <div style={{ display: 'flex', alignItems: 'center' }}>
          <div style={{ color: 'white', fontSize: '20px', marginRight: '40px' }}>
            用户部门管理系统
          </div>
          <div style={{ display: 'flex', gap: '16px' }}>
            <Button
              type="primary"
              size="large"
              icon={<HomeOutlined />}
              onClick={() => navigate('/')}
            >
              首页
            </Button>
            <Button
              type="primary"
              size="large"
              icon={<TeamOutlined />}
              onClick={() => navigate('/users')}
            >
              用户管理
            </Button>
            <Button
              type="primary"
              size="large"
              icon={<ApartmentOutlined />}
              onClick={() => navigate('/departments')}
            >
              部门管理
            </Button>
          </div>
        </div>
        <div>
          <Dropdown menu={{ items: userMenuItems }} placement="bottomRight">
            <Button type="text" style={{ color: 'white' }}>
              <Space>
                <Avatar size="small" icon={<UserOutlined />} />
                {user?.realname || user?.username}
              </Space>
            </Button>
          </Dropdown>
        </div>
      </Header>
      <Content style={{ padding: '24px', margin: 0, minHeight: 280 }}>
        {children}
      </Content>
      <Footer style={{ textAlign: 'center' }}>
        © 2024 用户部门管理系统
      </Footer>
    </Layout>
  );
};

export default AppLayout;
