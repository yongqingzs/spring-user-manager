import React, { useState, useEffect } from 'react';
import { 
  Card, 
  Table, 
  Button, 
  Space, 
  Input, 
  Modal, 
  Form, 
  message, 
  Popconfirm,
  Switch,
  Select,
  Checkbox
} from 'antd';
import { PlusOutlined, EditOutlined, DeleteOutlined, SearchOutlined } from '@ant-design/icons';
import { userService, User, UserRequest } from '../services/user';
import { departmentService, Department } from '../services/department';
import type { ColumnsType } from 'antd/es/table';

const { Search } = Input;
const { Option } = Select;

const UserManagePage: React.FC = () => {
  const [users, setUsers] = useState<User[]>([]);
  const [departments, setDepartments] = useState<Department[]>([]);
  const [loading, setLoading] = useState(false);
  const [modalVisible, setModalVisible] = useState(false);
  const [editingUser, setEditingUser] = useState<User | null>(null);
  const [changePassword, setChangePassword] = useState(false);
  const [pagination, setPagination] = useState({
    current: 1,
    pageSize: 10,
    total: 0,
  });
  const [searchQuery, setSearchQuery] = useState('');
  const [form] = Form.useForm();

  const fetchUsers = async (page = 1, pageSize = 10, query = '') => {
    setLoading(true);
    try {
      const response = await userService.getUsers(page, pageSize, query);
      // console.log('Raw response data:', response.data.data);
      const { list: records, total, current, size } = response.data.data;
      console.log('Parsed user records:', records);
      setUsers(records);
      setPagination({
        current,
        pageSize: size,
        total,
      });
    } catch (error) {
      message.error('获取用户列表失败');
    } finally {
      setLoading(false);
    }
  };

  const fetchDepartments = async () => {
    try {
      const response = await departmentService.getDepartments(1, 1000); // 获取所有部门
      setDepartments(response.data.data.list);
    } catch (error) {
      message.error('获取部门列表失败');
    }
  };

  useEffect(() => {
    fetchUsers();
    fetchDepartments();
  }, []);

  const handleTableChange = (paginationConfig: any) => {
    fetchUsers(paginationConfig.current, paginationConfig.pageSize, searchQuery);
  };

  const handleSearch = (value: string) => {
    setSearchQuery(value);
    fetchUsers(1, pagination.pageSize, value);
  };

  const handleAdd = () => {
    setEditingUser(null);
    setChangePassword(false);
    form.resetFields();
    setModalVisible(true);
  };

  const handleEdit = (user: User) => {
    setEditingUser(user);
    setChangePassword(false);
    form.setFieldsValue({
      ...user,
      departmentCodes: user.departmentCodes || [], // 确保 departmentCodes 是一个数组
      password: '', // 清空密码字段
    });
    setModalVisible(true);
  };

  const handleDelete = async (id: number) => {
    try {
      await userService.deleteUser(id);
      message.success('删除成功');
      fetchUsers(pagination.current, pagination.pageSize, searchQuery);
    } catch (error) {
      message.error('删除失败');
    }
  };

  const handleStatusChange = async (id: number, enabled: boolean) => {
    console.log('Attempting to update user status:', id, enabled);
    try {
      await userService.updateUserStatus(id, enabled);
      message.success('状态更新成功');
      fetchUsers(pagination.current, pagination.pageSize, searchQuery);
    } catch (error) {
      console.error('Error updating user status:', error);
      message.error('状态更新失败');
    }
  };

  const handleModalOk = async () => {
    try {
      const values = await form.validateFields();
      const userRequest: UserRequest = {
        username: values.username,
        realname: values.realname,
        email: values.email,
        mobile: values.mobile,
        departmentCode: values.departmentCode,
      };

      // 如果是新用户或者选择了修改密码，则包含密码
      if (!editingUser || changePassword) {
        userRequest.password = values.password;
      }

      if (editingUser) {
        await userService.updateUser(editingUser.id, userRequest);
        message.success('更新成功');
      } else {
        await userService.createUser(userRequest);
        message.success('创建成功');
      }

      setModalVisible(false);
      fetchUsers(pagination.current, pagination.pageSize, searchQuery);
    } catch (error) {
      message.error(editingUser ? '更新失败' : '创建失败');
    }
  };

  const getDepartmentName = (departmentCodes?: string[]) => {
    if (!departmentCodes || departmentCodes.length === 0) return '无';
    const names = departmentCodes.map(code => {
      const dept = departments.find(d => d.code === code);
      return dept ? dept.name : code;
    });
    return names.join(', ');
  };

  const columns: ColumnsType<User> = [
    {
      title: 'ID',
      dataIndex: 'id',
      key: 'id',
      width: 80,
    },
    {
      title: '用户名',
      dataIndex: 'username',
      key: 'username',
    },
    {
      title: '真实姓名',
      dataIndex: 'realname',
      key: 'realname',
    },
    {
      title: '邮箱',
      dataIndex: 'email',
      key: 'email',
    },
    {
      title: '手机号',
      dataIndex: 'mobile',
      key: 'mobile',
    },
    {
      title: '部门',
      dataIndex: 'departmentCodes', // 修改为 departmentCodes
      key: 'departmentCodes',
      render: (departmentCodes: string[]) => getDepartmentName(departmentCodes),
    },
    {
      title: '状态',
      dataIndex: 'enabled',
      key: 'enabled',
      render: (enabled: boolean, record: User) => (
        <Switch
          checked={enabled}
          onChange={(checked) => handleStatusChange(record.id, checked)}
          checkedChildren="启用"
          unCheckedChildren="禁用"
        />
      ),
    },
    {
      title: '操作',
      key: 'actions',
      render: (_, record: User) => (
        <Space size="middle">
          <Button
            type="link"
            icon={<EditOutlined />}
            onClick={() => handleEdit(record)}
          >
            编辑
          </Button>
          <Popconfirm
            title="确定要删除这个用户吗？"
            onConfirm={() => handleDelete(record.id)}
            okText="确定"
            cancelText="取消"
          >
            <Button
              type="link"
              danger
              icon={<DeleteOutlined />}
            >
              删除
            </Button>
          </Popconfirm>
        </Space>
      ),
    },
  ];

  return (
    <div>
      <Card
        title="用户管理"
        extra={
          <Button type="primary" icon={<PlusOutlined />} onClick={handleAdd}>
            添加用户
          </Button>
        }
      >
        <div style={{ marginBottom: 16 }}>
          <Search
            placeholder="搜索用户..."
            allowClear
            enterButton={<SearchOutlined />}
            size="large"
            onSearch={handleSearch}
            style={{ width: 300 }}
          />
        </div>

        <Table
          columns={columns}
          dataSource={users}
          rowKey="id"
          loading={loading}
          pagination={pagination}
          onChange={handleTableChange}
        />
      </Card>

      <Modal
        title={editingUser ? '编辑用户' : '添加用户'}
        open={modalVisible}
        onOk={handleModalOk}
        onCancel={() => setModalVisible(false)}
        destroyOnClose
      >
        <Form
          form={form}
          layout="vertical"
          initialValues={{ enabled: true }}
        >
          <Form.Item
            name="username"
            label="用户名"
            rules={[{ required: true, message: '请输入用户名' }]}
          >
            <Input disabled={!!editingUser} />
          </Form.Item>

          <Form.Item
            name="realname"
            label="真实姓名"
            rules={[{ required: true, message: '请输入真实姓名' }]}
          >
            <Input />
          </Form.Item>

          <Form.Item
            name="email"
            label="邮箱"
            rules={[{ type: 'email', message: '请输入有效的邮箱地址' }]}
          >
            <Input />
          </Form.Item>

          <Form.Item
            name="mobile"
            label="手机号"
          >
            <Input />
          </Form.Item>

          <Form.Item
            name="departmentCode"
            label="所属部门"
          >
            <Select placeholder="请选择部门" allowClear>
              {departments.map(dept => (
                <Option key={dept.id} value={dept.code}>
                  {dept.name} ({dept.code})
                </Option>
              ))}
            </Select>
          </Form.Item>

          {editingUser && (
            <Form.Item>
              <Checkbox
                checked={changePassword}
                onChange={(e) => setChangePassword(e.target.checked)}
              >
                修改密码
              </Checkbox>
            </Form.Item>
          )}

          {(!editingUser || changePassword) && (
            <Form.Item
              name="password"
              label="密码"
              rules={[{ required: !editingUser || changePassword, message: '请输入密码' }]}
            >
              <Input.Password placeholder={editingUser ? '输入新密码' : '请输入密码'} />
            </Form.Item>
          )}
        </Form>
      </Modal>
    </div>
  );
};

export default UserManagePage;
