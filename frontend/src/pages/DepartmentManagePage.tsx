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
  Tree,
  Row,
  Col,
  Select
} from 'antd';
import { EditOutlined, DeleteOutlined, SearchOutlined, PlusOutlined } from '@ant-design/icons';
import { departmentService, Department, DepartmentRequest } from '../services/department';
import type { ColumnsType } from 'antd/es/table';

const { Search } = Input;

const DepartmentManagePage: React.FC = () => {
  const [departments, setDepartments] = useState<Department[]>([]);
  const [departmentTree, setDepartmentTree] = useState<Department[]>([]);
  const [loading, setLoading] = useState(false);
  const [modalVisible, setModalVisible] = useState(false);
  const [editingDepartment, setEditingDepartment] = useState<Department | null>(null);
  const [pagination, setPagination] = useState({
    current: 1,
    pageSize: 10,
    total: 0,
  });
  const [searchQuery, setSearchQuery] = useState('');
  const [form] = Form.useForm();
  const [treeModalVisible, setTreeModalVisible] = useState(false);

  const fetchDepartments = async (page = 1, pageSize = 10, search = '') => {
    setLoading(true);
    try {
      const response = await departmentService.getDepartments(page, pageSize, search);
      console.log('Raw department response data:', response.data.data);
      const { list: records, total, current, size } = response.data.data;
      console.log('Parsed department records:', records);
      setDepartments(records);
      setPagination({
        current,
        pageSize: size,
        total,
      });
    } catch (error) {
      message.error('获取部门列表失败');
    } finally {
      setLoading(false);
    }
  };

  const fetchDepartmentTree = async () => {
    try {
      const response = await departmentService.getDepartmentTree();
      setDepartmentTree(response.data.data);
    } catch (error) {
      message.error('获取部门树失败');
    }
  };

  useEffect(() => {
    fetchDepartments();
    fetchDepartmentTree();
  }, []);

  const handleTableChange = (paginationConfig: any) => {
    fetchDepartments(paginationConfig.current, paginationConfig.pageSize, searchQuery);
  };

  const handleSearch = (value: string) => {
    setSearchQuery(value);
    fetchDepartments(1, pagination.pageSize, value);
  };

  const handleEdit = (department: Department) => {
    setEditingDepartment(department);
    form.setFieldsValue(department);
    setModalVisible(true);
  };

  const handleDelete = async (id: number) => {
    try {
      await departmentService.deleteDepartment(id);
      message.success('删除成功');
      fetchDepartments(pagination.current, pagination.pageSize, searchQuery);
      fetchDepartmentTree();
    } catch (error) {
      message.error('删除失败');
    }
  };

  const handleModalOk = async () => {
    try {
      const values = await form.validateFields();
      const departmentRequest: DepartmentRequest = {
        code: values.code,
        name: values.name,
        parentCode: values.parentCode,
        description: values.description,
      };

      if (editingDepartment) {
        await departmentService.updateDepartment(editingDepartment.id, departmentRequest);
        message.success('更新成功');
      } else {
        await departmentService.createDepartment(departmentRequest);
        message.success('创建成功');
      }

      setModalVisible(false);
      fetchDepartments(pagination.current, pagination.pageSize, searchQuery);
      fetchDepartmentTree();
    } catch (error) {
      message.error(editingDepartment ? '更新失败' : '创建失败');
    }
  };

  const handleAdd = () => {
    setEditingDepartment(null);
    form.resetFields();
    setModalVisible(true);
  };

  const convertTreeData = (departments: Department[]): any[] => {
    return departments.map(dept => ({
      title: `${dept.name} (${dept.code})`,
      key: dept.code,
      children: dept.children ? convertTreeData(dept.children) : undefined,
    }));
  };

  const columns: ColumnsType<Department> = [
    {
      title: 'ID',
      dataIndex: 'id',
      key: 'id',
      width: 80,
    },
    {
      title: '部门编码',
      dataIndex: 'code',
      key: 'code',
    },
    {
      title: '部门名称',
      dataIndex: 'name',
      key: 'name',
    },
    {
      title: '上级部门',
      dataIndex: 'parentCode',
      key: 'parentCode',
      render: (parentCode: string) => parentCode || '无',
    },
    {
      title: '描述',
      dataIndex: 'description',
      key: 'description',
      ellipsis: true,
    },
    {
      title: '操作',
      key: 'actions',
      render: (_, record: Department) => (
        <Space size="middle">
          <Button
            type="link"
            icon={<EditOutlined />}
            onClick={() => handleEdit(record)}
          >
            编辑
          </Button>
          <Popconfirm
            title="确定要删除这个部门吗？"
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
      <Row gutter={16}>
        <Col span={24}>
          <Card
            title="部门管理"
            extra={
              <Button type="primary" icon={<PlusOutlined />} onClick={handleAdd}>
                添加部门
              </Button>
            }
          >
            <div style={{ marginBottom: 16, display: 'flex', justifyContent: 'space-between' }}>
              <Search
                placeholder="搜索部门..."
                allowClear
                enterButton={<SearchOutlined />}
                size="large"
                onSearch={handleSearch}
                style={{ width: 300 }}
              />
              <Space>
                <Button type="primary" onClick={() => setTreeModalVisible(true)}>
                  部门组织架构
                </Button>
              </Space>
            </div>
            <Table
              columns={columns}
              dataSource={departments}
              rowKey="id"
              loading={loading}
              pagination={pagination}
              onChange={handleTableChange}
            />
          </Card>
        </Col>
      </Row>

      <Modal
        title={editingDepartment ? '编辑部门' : '添加部门'}
        open={modalVisible}
        onOk={handleModalOk}
        onCancel={() => setModalVisible(false)}
        destroyOnClose
      >
        <Form
          form={form}
          layout="vertical"
        >
          <Form.Item
            name="code"
            label="部门编码"
            rules={[{ required: true, message: '请输入部门编码' }]}
          >
            <Input disabled={!!editingDepartment} />
          </Form.Item>

          <Form.Item
            name="name"
            label="部门名称"
            rules={[{ required: true, message: '请输入部门名称' }]}
          >
            <Input />
          </Form.Item>

          <Form.Item
            name="parentCode"
            label="上级部门编码"
          >
            <Select placeholder="请选择上级部门" allowClear>
              {departments.map(dept => (
                <Select.Option key={dept.code} value={dept.code}>
                  {dept.name} ({dept.code})
                </Select.Option>
              ))}
            </Select>
          </Form.Item>

          <Form.Item
            name="description"
            label="描述"
          >
            <Input.TextArea rows={4} />
          </Form.Item>
        </Form>
      </Modal>

      <Modal
        title="部门组织架构"
        open={treeModalVisible}
        onCancel={() => setTreeModalVisible(false)}
        footer={null}
      >
        <Tree
          treeData={convertTreeData(departmentTree)}
          defaultExpandAll
        />
      </Modal>
    </div>
  );
};

export default DepartmentManagePage;
