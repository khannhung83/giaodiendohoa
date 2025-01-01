package com.mycompany.fixdemo12;

import com.mycompany.fixdemo12.connect;
import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import javax.swing.JOptionPane;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;

public class frame extends javax.swing.JFrame {

    public frame() {
        initComponents();
        setTitle("Quản Lý Sinh Viên");
        setSize(888, 575); // Đặt kích thước JFrame (800px x 600px).
        setLocationRelativeTo(null); // Đặt JFrame ở giữa màn hình.
        setResizable(true); // Cho phép thay đổi kích thước 
        loadData();
    }

    private void customizeTableColumns() {
        // Kiểm tra và đặt kích thước các cột
        if (table.getColumnModel().getColumnCount() > 0) {
            table.getColumnModel().getColumn(0).setPreferredWidth(25);  // STT
            table.getColumnModel().getColumn(1).setPreferredWidth(50);  // ID
            table.getColumnModel().getColumn(2).setPreferredWidth(140); // Họ và Tên
            table.getColumnModel().getColumn(3).setPreferredWidth(70);  // Ngày Sinh
            table.getColumnModel().getColumn(4).setPreferredWidth(100); // Quê Quán
            table.getColumnModel().getColumn(5).setPreferredWidth(90);  // Số Điện Thoại
            table.getColumnModel().getColumn(6).setPreferredWidth(42);  // Điểm
        }
    }

    private void loadData() {
        connect cn = new connect(); // Kết nối cơ sở dữ liệu
        Connection conn = cn.getConnection();
        if (conn == null) {
            System.out.println("Khong the ket noi CSDL");
            return;
        }

        try {
            String sql = "SELECT * FROM tblStudent"; // Lệnh SQL để lấy dữ liệu
            Statement st = conn.createStatement();
            ResultSet rs = st.executeQuery(sql);

            // Tạo mô hình bảng
            DefaultTableModel model = new DefaultTableModel();
            model.addColumn("STT"); // Cột số thứ tự
            model.addColumn("ID");
            model.addColumn("Họ Tên");
            model.addColumn("Ngày sinh");
            model.addColumn("Địa chỉ");
            model.addColumn("Số điện thoại");
            model.addColumn("Điểm");

            SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
            // Đưa dữ liệu từ ResultSet vào bảng
            int stt = 1; // Bắt đầu từ số thứ tự 1
            while (rs.next()) {
                // Lấy ngày sinh từ cơ sở dữ liệu
                Date dob = rs.getDate("dob");
                String formattedDob = (dob != null) ? dateFormat.format(dob) : "";
                Object[] row = {
                    stt++, // Tăng số thứ tự
                    rs.getString("id"),
                    rs.getString("name"),
                    rs.getString("dob"),
                    rs.getString("address"),
                    rs.getString("phone"),
                    rs.getString("mark")
                };
                model.addRow(row);
            }

            // Hiển thị dữ liệu lên JTable
            table.setModel(model);

            customizeTableColumns();
            table.addMouseListener(new java.awt.event.MouseAdapter() {
                public void mouseClicked(java.awt.event.MouseEvent evt) {
                    int selectedRow = table.getSelectedRow(); // Lấy chỉ số dòng được chọn
                    if (selectedRow >= 0) {
                        // Hiển thị dữ liệu lên các JTextField
                        txtID.setText(table.getValueAt(selectedRow, 1).toString());
                        txtName.setText(table.getValueAt(selectedRow, 2).toString());
                        txtDob.setText(table.getValueAt(selectedRow, 3).toString());
                        txtAddress.setText(table.getValueAt(selectedRow, 4).toString());
                        txtPhone.setText(table.getValueAt(selectedRow, 5).toString());
                        txtMark.setText(table.getValueAt(selectedRow, 6).toString());
                    }
                }
            });
            st.close();
            rs.close();
            conn.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void deleteData(String id) {
        connect cn = new connect();
        Connection conn = cn.getConnection();
        if (conn == null) {
            System.out.println("Không thể kết nối cơ sở dữ liệu");
            return;
        }

        try {
            // Dùng PreparedStatement để tránh SQL Injection
            String sql = "DELETE FROM tblStudent WHERE id = ?"; // Sử dụng dấu hỏi cho tham số
            PreparedStatement pst = conn.prepareStatement(sql);
            pst.setString(1, id); // Gán giá trị cho tham số

            int affectedRows = pst.executeUpdate();

            if (affectedRows > 0) {
                System.out.println("Xóa thành công sinh viên với ID: " + id);
            } else {
                System.out.println("Không tìm thấy sinh viên với ID: " + id);
            }

            pst.close();
            conn.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void updateData(String id, String name, String dob, String address, String phone, String mark) {
        connect cn = new connect();
        Connection conn = cn.getConnection();
        if (conn == null) {
            System.out.println("Không thể kết nối CSDL");
            return;
        }

        try {
            // Chuyển đổi ngày sinh từ String sang java.sql.Date
            java.sql.Date sqlDob = null;
            if (dob != null && !dob.isEmpty()) {
                try {
                    java.util.Date utilDob = new java.text.SimpleDateFormat("yyyy-MM-dd").parse(dob); // Định dạng ngày nhập vào
                    sqlDob = new java.sql.Date(utilDob.getTime()); // Chuyển đổi thành java.sql.Date
                } catch (java.text.ParseException e) {
                    System.out.println("Ngày sinh không hợp lệ: " + dob);
                    return;
                }
            }

            // Chuẩn bị truy vấn SQL để cập nhật dữ liệu
            String sql = "UPDATE tblStudent SET name = ?, dob = ?, address = ?, phone = ?, mark = ? WHERE id = ?";
            PreparedStatement pst = conn.prepareStatement(sql);

            // Thiết lập các tham số cho PreparedStatement
            pst.setString(1, name);
            pst.setDate(2, sqlDob);  // Set ngày sinh dưới dạng java.sql.Date
            pst.setString(3, address);
            pst.setString(4, phone);
            pst.setString(5, mark);
            pst.setString(6, id);

            // Thực thi truy vấn và kiểm tra kết quả
            int rowsUpdated = pst.executeUpdate();
            if (rowsUpdated > 0) {
                System.out.println("Cap nhat thanh cong.");
            } else {
                System.out.println("Khong co dong nao duoc cap nhat.");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void searchStudentByID(String id) {
        connect cn = new connect();
        Connection conn = cn.getConnection();
        if (conn == null) {
            System.out.println("Không thể kết nối cơ sở dữ liệu");
            return;
        }

        try {
            String sql = "SELECT * FROM tblStudent WHERE id = ?"; // Truy vấn SQL để tìm sinh viên theo ID
            PreparedStatement preparedStatement = conn.prepareStatement(sql);
            preparedStatement.setString(1, id);

            ResultSet rs = preparedStatement.executeQuery();

            // Tạo mô hình bảng để hiển thị kết quả
            DefaultTableModel model = new DefaultTableModel();
            model.addColumn("STT");
            model.addColumn("ID");
            model.addColumn("Họ Tên");
            model.addColumn("Ngày sinh");
            model.addColumn("Địa chỉ");
            model.addColumn("Số điện thoại");
            model.addColumn("Điểm");

            // Kiểm tra nếu tìm thấy kết quả
            if (rs.next()) {
                // Định dạng ngày sinh theo kiểu yyyy-MM-dd
                Date dob = rs.getDate("dob");
                String formattedDob = dob != null ? new java.text.SimpleDateFormat("yyyy-MM-dd").format(dob) : "";

                Object[] row = {
                    1, // STT có thể là 1, nếu chỉ có một kết quả tìm được
                    rs.getString("id"),
                    rs.getString("name"),
                    formattedDob, // Hiển thị ngày sinh đã được định dạng
                    rs.getString("address"),
                    rs.getString("phone"),
                    rs.getString("mark")
                };
                model.addRow(row);
            } else {
                JOptionPane.showMessageDialog(this, "Không tìm thấy sinh viên với ID: " + id);
            }

            // Hiển thị dữ liệu lên JTable
            table.setModel(model);

            rs.close();
            conn.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // Phương thức kiểm tra ngày hợp lệ
    private boolean isValidDate(String date) {
        // Chuẩn hóa: thay thế tất cả dấu '/' thành '-'
        //date = date.replace('/', '-');

        try {
            // Kiểm tra định dạng "yyyy-MM-dd"
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
            sdf.setLenient(false); // Không chấp nhận ngày tự động điều chỉnh
            sdf.parse(date);
            return true; // Ngày hợp lệ

        } catch (ParseException e) {
            return false; // Ngày không hợp lệ
        }
    }

    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jPanel8 = new javax.swing.JPanel();
        jLabel13 = new javax.swing.JLabel();
        jPanel7 = new javax.swing.JPanel();
        jLabel14 = new javax.swing.JLabel();
        jLabel15 = new javax.swing.JLabel();
        jLabel16 = new javax.swing.JLabel();
        jLabel17 = new javax.swing.JLabel();
        jLabel18 = new javax.swing.JLabel();
        txtID = new javax.swing.JTextField();
        jButton3 = new javax.swing.JButton();
        jButton10 = new javax.swing.JButton();
        jButton11 = new javax.swing.JButton();
        jButton12 = new javax.swing.JButton();
        jLabel19 = new javax.swing.JLabel();
        txtDob = new javax.swing.JTextField();
        txtAddress = new javax.swing.JTextField();
        txtName = new javax.swing.JTextField();
        txtPhone = new javax.swing.JTextField();
        jButton14 = new javax.swing.JButton();
        jButton1 = new javax.swing.JButton();
        jLabel2 = new javax.swing.JLabel();
        txtMark = new javax.swing.JTextField();
        jScrollPane2 = new javax.swing.JScrollPane();
        table = new javax.swing.JTable();
        jLabel20 = new javax.swing.JLabel();
        jLabel1 = new javax.swing.JLabel();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setTitle("Quản Lí Sinh Viên");
        setResizable(false);

        jPanel8.setBackground(new java.awt.Color(255, 255, 255));

        jLabel13.setBackground(new java.awt.Color(204, 255, 255));
        jLabel13.setFont(new java.awt.Font("Segoe UI Semibold", 1, 24)); // NOI18N
        jLabel13.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel13.setIcon(new javax.swing.ImageIcon("C:\\Users\\admin\\Downloads\\multiple-users-silhouette (1).png")); // NOI18N
        jLabel13.setText("Quản Lí Sinh Viên");
        jLabel13.setOpaque(true);

        jPanel7.setBackground(new java.awt.Color(204, 204, 204));

        jLabel14.setText("ID Sinh Viên:");

        jLabel15.setText("Tên Sinh Viên:");

        jLabel16.setText("Quê Quán:");

        jLabel17.setText("Ngày Sinh:");

        jLabel18.setText("Số Điện Thoại:");

        txtID.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtIDActionPerformed(evt);
            }
        });

        jButton3.setBackground(new java.awt.Color(153, 255, 255));
        jButton3.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        jButton3.setIcon(new javax.swing.ImageIcon("C:\\Users\\admin\\Downloads\\add.png")); // NOI18N
        jButton3.setText("Thêm");
        jButton3.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton3ActionPerformed(evt);
            }
        });

        jButton10.setBackground(new java.awt.Color(153, 255, 255));
        jButton10.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        jButton10.setIcon(new javax.swing.ImageIcon("C:\\Users\\admin\\Downloads\\edit.png")); // NOI18N
        jButton10.setText("Sửa");
        jButton10.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton10ActionPerformed(evt);
            }
        });

        jButton11.setBackground(new java.awt.Color(153, 255, 255));
        jButton11.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        jButton11.setIcon(new javax.swing.ImageIcon("C:\\Users\\admin\\Downloads\\delete.png")); // NOI18N
        jButton11.setText("Xóa");
        jButton11.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton11ActionPerformed(evt);
            }
        });

        jButton12.setBackground(new java.awt.Color(153, 255, 255));
        jButton12.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        jButton12.setIcon(new javax.swing.ImageIcon("C:\\Users\\admin\\Downloads\\loupe.png")); // NOI18N
        jButton12.setText("Tìm Kiếm");
        jButton12.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton12ActionPerformed(evt);
            }
        });

        jLabel19.setBackground(new java.awt.Color(255, 255, 102));
        jLabel19.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        jLabel19.setForeground(new java.awt.Color(51, 51, 51));
        jLabel19.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        jLabel19.setIcon(new javax.swing.ImageIcon("C:\\Users\\admin\\Downloads\\profile.png")); // NOI18N
        jLabel19.setText("Thông Tin Sinh Viên");
        jLabel19.setHorizontalTextPosition(javax.swing.SwingConstants.RIGHT);

        jButton14.setBackground(new java.awt.Color(153, 255, 255));
        jButton14.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        jButton14.setIcon(new javax.swing.ImageIcon("C:\\Users\\admin\\Downloads\\exit.png")); // NOI18N
        jButton14.setText("Thoát");
        jButton14.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton14ActionPerformed(evt);
            }
        });

        jButton1.setBackground(new java.awt.Color(153, 255, 255));
        jButton1.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        jButton1.setIcon(new javax.swing.ImageIcon("C:\\Users\\admin\\Downloads\\broom.png")); // NOI18N
        jButton1.setText("Làm Mới");
        jButton1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton1ActionPerformed(evt);
            }
        });

        jLabel2.setText("Điểm Trung Bình:");

        txtMark.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtMarkActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel7Layout = new javax.swing.GroupLayout(jPanel7);
        jPanel7.setLayout(jPanel7Layout);
        jPanel7Layout.setHorizontalGroup(
            jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel7Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel7Layout.createSequentialGroup()
                        .addComponent(jLabel19, javax.swing.GroupLayout.PREFERRED_SIZE, 165, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel7Layout.createSequentialGroup()
                        .addGroup(jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addGroup(javax.swing.GroupLayout.Alignment.LEADING, jPanel7Layout.createSequentialGroup()
                                .addComponent(jLabel14, javax.swing.GroupLayout.PREFERRED_SIZE, 74, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(0, 0, Short.MAX_VALUE))
                            .addGroup(jPanel7Layout.createSequentialGroup()
                                .addGap(0, 0, Short.MAX_VALUE)
                                .addGroup(jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addGroup(jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                                        .addComponent(jLabel15, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                        .addComponent(jLabel17, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                                    .addComponent(jLabel16, javax.swing.GroupLayout.PREFERRED_SIZE, 75, javax.swing.GroupLayout.PREFERRED_SIZE))
                                .addGap(20, 20, 20)
                                .addGroup(jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                                    .addComponent(txtDob, javax.swing.GroupLayout.PREFERRED_SIZE, 139, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(txtName, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, 140, Short.MAX_VALUE)
                                    .addComponent(txtID, javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(txtAddress))
                                .addGap(20, 20, 20))
                            .addGroup(jPanel7Layout.createSequentialGroup()
                                .addGroup(jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                                    .addGroup(javax.swing.GroupLayout.Alignment.LEADING, jPanel7Layout.createSequentialGroup()
                                        .addGroup(jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                                            .addComponent(jButton11, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                            .addComponent(jButton3, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                            .addComponent(jButton1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                                        .addGap(18, 18, 18)
                                        .addGroup(jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                            .addComponent(jButton14, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                            .addComponent(jButton12, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                            .addComponent(jButton10, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
                                    .addGroup(jPanel7Layout.createSequentialGroup()
                                        .addGroup(jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                            .addComponent(jLabel18, javax.swing.GroupLayout.PREFERRED_SIZE, 89, javax.swing.GroupLayout.PREFERRED_SIZE)
                                            .addComponent(jLabel2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                        .addGroup(jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                            .addComponent(txtMark, javax.swing.GroupLayout.DEFAULT_SIZE, 140, Short.MAX_VALUE)
                                            .addComponent(txtPhone))))
                                .addGap(19, 19, 19)))
                        .addGap(45, 45, 45))))
        );
        jPanel7Layout.setVerticalGroup(
            jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel7Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel19, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel14, javax.swing.GroupLayout.PREFERRED_SIZE, 38, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(txtID, javax.swing.GroupLayout.PREFERRED_SIZE, 38, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel15, javax.swing.GroupLayout.PREFERRED_SIZE, 38, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(txtName, javax.swing.GroupLayout.PREFERRED_SIZE, 38, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel17, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(txtDob, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel16, javax.swing.GroupLayout.PREFERRED_SIZE, 36, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(txtAddress, javax.swing.GroupLayout.PREFERRED_SIZE, 39, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel18, javax.swing.GroupLayout.PREFERRED_SIZE, 36, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(txtPhone, javax.swing.GroupLayout.PREFERRED_SIZE, 36, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(txtMark, javax.swing.GroupLayout.PREFERRED_SIZE, 36, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addGroup(jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jButton3, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jButton10, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jButton12, javax.swing.GroupLayout.PREFERRED_SIZE, 33, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jButton11, javax.swing.GroupLayout.PREFERRED_SIZE, 33, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(jButton14, javax.swing.GroupLayout.DEFAULT_SIZE, 35, Short.MAX_VALUE)
                    .addComponent(jButton1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addGap(21, 21, 21))
        );

        table.setBackground(new java.awt.Color(214, 217, 217));
        table.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "STT", "ID", "Họ và Tên", "Ngày Sinh", "Quê Quán", "Số Điện Thoại", "Điểm"
            }
        ));
        jScrollPane2.setViewportView(table);
        if (table.getColumnModel().getColumnCount() > 0) {
            table.getColumnModel().getColumn(0).setPreferredWidth(25);
            table.getColumnModel().getColumn(1).setPreferredWidth(50);
            table.getColumnModel().getColumn(2).setPreferredWidth(140);
            table.getColumnModel().getColumn(3).setPreferredWidth(70);
            table.getColumnModel().getColumn(4).setPreferredWidth(100);
            table.getColumnModel().getColumn(6).setPreferredWidth(42);
        }

        jLabel20.setBackground(new java.awt.Color(204, 255, 204));
        jLabel20.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        jLabel20.setIcon(new javax.swing.ImageIcon("C:\\Users\\admin\\Downloads\\list.png")); // NOI18N
        jLabel20.setText(" Danh Sách Sinh Viên");
        jLabel20.setOpaque(true);

        jLabel1.setIcon(new javax.swing.ImageIcon("C:\\Users\\admin\\Downloads\\Logo_trường_Đại_học_Công_nghệ_thông_tin_và_Truyền_thông_Việt_-_Hàn,_Đại_học_Đà_Nẵng.jpg")); // NOI18N

        javax.swing.GroupLayout jPanel8Layout = new javax.swing.GroupLayout(jPanel8);
        jPanel8.setLayout(jPanel8Layout);
        jPanel8Layout.setHorizontalGroup(
            jPanel8Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel8Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel8Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel8Layout.createSequentialGroup()
                        .addComponent(jPanel7, javax.swing.GroupLayout.PREFERRED_SIZE, 263, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel8Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jScrollPane2, javax.swing.GroupLayout.DEFAULT_SIZE, 640, Short.MAX_VALUE)
                            .addComponent(jLabel20, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                        .addContainerGap())
                    .addGroup(jPanel8Layout.createSequentialGroup()
                        .addComponent(jLabel13, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jLabel1)
                        .addGap(26, 26, 26))))
        );
        jPanel8Layout.setVerticalGroup(
            jPanel8Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel8Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel8Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel13, javax.swing.GroupLayout.PREFERRED_SIZE, 47, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel1, javax.swing.GroupLayout.PREFERRED_SIZE, 53, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel8Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel8Layout.createSequentialGroup()
                        .addComponent(jLabel20)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE))
                    .addComponent(jPanel7, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jPanel8, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jPanel8, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addContainerGap())
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void txtIDActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtIDActionPerformed
    }//GEN-LAST:event_txtIDActionPerformed
    private void jButton3ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton3ActionPerformed
        String id = txtID.getText().trim();
        String name = txtName.getText().trim();
        String dob = txtDob.getText().trim();
        String address = txtAddress.getText().trim();
        String phone = txtPhone.getText().trim();
        String mark = txtMark.getText().trim();

        // Kiểm tra dữ liệu đầu vào
        if (id.isEmpty() || name.isEmpty() || dob.isEmpty() || address.isEmpty() || phone.isEmpty() || mark.isEmpty()) {
            javax.swing.JOptionPane.showMessageDialog(null, "Vui lòng điền đầy đủ thông tin vào các ô trống");
            return;
        }
        // Chuẩn hóa định dạng ngày (thay thế '/' thành '-')
        dob = dob.replace('/', '-');
        // Kiểm tra định dạng ngày sinh hợp lệ
        java.sql.Date sqlDob = null;
        try {
            java.util.Date utilDob = new java.text.SimpleDateFormat("yyyy-MM-dd").parse(dob); // Định dạng ngày nhập vào
            sqlDob = new java.sql.Date(utilDob.getTime()); // Chuyển đổi thành java.sql.Date
        } catch (java.text.ParseException e) {
            javax.swing.JOptionPane.showMessageDialog(null, "Ngày sinh không hợp lệ. Định dạng ngày: yyyy-MM-dd hoặc yyyy/MM/dd");
            return;
        }

        connect cn = new connect(); // Kết nối cơ sở dữ liệu
        try (Connection conn = cn.getConnection()) {
            if (conn == null) {
                javax.swing.JOptionPane.showMessageDialog(null, "Không thể kết nối cơ sở dữ liệu");
                return;
            }

            String sql = "INSERT INTO tblStudent (id, name, dob, address, phone, mark) VALUES (?, ?, ?, ?, ?, ?)";
            try (PreparedStatement pst = conn.prepareStatement(sql)) {
                pst.setString(1, id);
                pst.setString(2, name);
                pst.setDate(3, sqlDob);  // Sử dụng java.sql.Date cho ngày sinh
                pst.setString(4, address);
                pst.setString(5, phone);
                pst.setString(6, mark);

                int rowsAffected = pst.executeUpdate();
                if (rowsAffected > 0) {
                    javax.swing.JOptionPane.showMessageDialog(null, "Thêm sinh viên thành công.");
                } else {
                    javax.swing.JOptionPane.showMessageDialog(null, "Không có dòng nào được thêm.");
                }

                // Làm mới bảng dữ liệu
                loadData();

                // Làm mới các textfield
                txtID.setText("");
                txtName.setText("");
                txtDob.setText("");
                txtAddress.setText("");
                txtPhone.setText("");
                txtMark.setText("");
            } catch (SQLException e) {
                if (e.getErrorCode() == 2627 || e.getErrorCode() == 2601
                        || e.getMessage().contains("Violation of PRIMARY KEY constraint")) {
                    // Xử lý lỗi trùng khóa

                    javax.swing.JOptionPane.showMessageDialog(null, "Không thể thêm sinh viên. ID đã tồn tại trong hệ thống.", "Lỗi trùng khóa chính", javax.swing.JOptionPane.ERROR_MESSAGE);
                } else {
                    javax.swing.JOptionPane.showMessageDialog(null, "Lỗi cơ sở dữ liệu: " + e.getMessage());
                }
                e.printStackTrace(); // In chi tiết lỗi SQL ra console
            }
        } catch (SQLException e) {
            e.printStackTrace(); // In chi tiết lỗi khi kết nối CSDL
        } catch (Exception e) {
            e.printStackTrace(); // In chi tiết lỗi khác ra console
        }

    }//GEN-LAST:event_jButton3ActionPerformed
    private void jButton11ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton11ActionPerformed
        // Lấy dòng được chọn trong bảng
        int selectedRow = table.getSelectedRow();

        if (selectedRow != -1) {
            // Lấy ID của sinh viên từ bảng
            String selectedID = table.getValueAt(selectedRow, 1).toString(); // Cột ID

            try {
                // Kết nối cơ sở dữ liệu
                connect db = new connect();
                Connection conn = db.getConnection();

                // Thực hiện lệnh xóa dữ liệu trong cơ sở dữ liệu
                String sql = "DELETE FROM tblStudent WHERE id = ?";
                PreparedStatement pst = conn.prepareStatement(sql);
                pst.setString(1, selectedID);

                int rowsAffected = pst.executeUpdate(); // Thực thi lệnh xóa

                if (rowsAffected > 0) {
                    // Nếu xóa thành công
                    DefaultTableModel model = (DefaultTableModel) table.getModel();
                    model.removeRow(selectedRow); // Xóa dòng trong bảng

                    // Làm rỗng các textfield
                    txtID.setText("");
                    txtName.setText("");
                    txtDob.setText("");
                    txtAddress.setText("");
                    txtPhone.setText("");
                    txtMark.setText("");
                } else {
                    JOptionPane.showMessageDialog(this, "Không tìm thấy sinh viên với ID: " + selectedID);
                }

                // Đóng tài nguyên
                pst.close();
                conn.close();

            } catch (SQLException e) {
                e.printStackTrace();
                JOptionPane.showMessageDialog(this, "Lỗi khi xóa dữ liệu từ cơ sở dữ liệu.");
            }
        } else {
            JOptionPane.showMessageDialog(this, "Vui lòng chọn một dòng trong bảng để xóa.");
        }
    }//GEN-LAST:event_jButton11ActionPerformed
    private void jButton14ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton14ActionPerformed
        System.exit(0);        // TODO add your handling code here:
    }//GEN-LAST:event_jButton14ActionPerformed
    private void jButton1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton1ActionPerformed
        //nut lam moi, clear het du lieu trong textfield
        txtID.setText("");
        txtName.setText("");
        txtDob.setText("");
        txtAddress.setText("");
        txtPhone.setText("");
        txtMark.setText("");
        //lam moi bang bang cach xoa va lay lai tu csdl
        try {
            // Kết nối tới cơ sở dữ liệu
            connect db = new connect(); // Đây là class bạn đã cung cấp
            Connection conn = db.getConnection();

            // Lấy dữ liệu từ cơ sở dữ liệu
            String sql = "SELECT * FROM tblStudent";
            PreparedStatement pst = conn.prepareStatement(sql);
            ResultSet rs = pst.executeQuery();

            // Xóa dữ liệu hiện tại trong bảng
            DefaultTableModel model = (DefaultTableModel) table.getModel();
            model.setRowCount(0); // Xóa toàn bộ các dòng hiện có

            // Thêm dữ liệu mới vào bảng
            while (rs.next()) {
                model.addRow(new Object[]{
                    model.getRowCount() + 1,
                    rs.getString("id"),
                    rs.getString("name"),
                    rs.getString("dob"),
                    rs.getString("address"),
                    rs.getString("phone"),
                    rs.getString("mark")
                });
            }

            // Đóng kết nối và tài nguyên
            rs.close();
            pst.close();
            conn.close();

        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Lỗi khi làm mới dữ liệu từ cơ sở dữ liệu.");
        }
    }//GEN-LAST:event_jButton1ActionPerformed
    private void txtMarkActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtMarkActionPerformed
    }//GEN-LAST:event_txtMarkActionPerformed
    private void jButton10ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton10ActionPerformed
    int selectedRow = table.getSelectedRow();
    if (selectedRow != -1) {
        // Lấy ID ban đầu từ bảng
        String oldId = table.getValueAt(selectedRow, 0).toString(); // Cột 0 là ID
        // Lấy ID hiện tại từ JTextField
        String newId = txtID.getText();

        // Kiểm tra nếu ID bị sửa đổi
        if (!oldId.equals(newId.trim())) {
            JOptionPane.showMessageDialog(this, "Không thể sửa ID. ID là khóa chính và không được thay đổi.");
            // Gọi lại hàm để đồng bộ dữ liệu từ bảng lên text field
            
            return;
        }

        // Các xử lý cập nhật khác
        String name = txtName.getText();
        String dob = txtDob.getText();
        String address = txtAddress.getText();
        String phone = txtPhone.getText();
        String mark = txtMark.getText();

        if (!isValidDate(dob)) {
            JOptionPane.showMessageDialog(this, "Ngày sinh không hợp lệ. Vui lòng nhập lại.");
            return;
        }

        if (name.isEmpty() || dob.isEmpty() || address.isEmpty() || phone.isEmpty() || mark.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Vui lòng điền đầy đủ thông tin.");
            return;
        }

        try {
            // Cập nhật cơ sở dữ liệu
            updateData(oldId, name, dob, address, phone, mark);

            // Cập nhật lại dữ liệu trên bảng
            table.setValueAt(name, selectedRow, 1);
            table.setValueAt(dob, selectedRow, 2);
            table.setValueAt(address, selectedRow, 3);
            table.setValueAt(phone, selectedRow, 4);
            table.setValueAt(mark, selectedRow, 5);

            JOptionPane.showMessageDialog(this, "Dữ liệu đã được sửa!");
        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Đã xảy ra lỗi khi sửa dữ liệu.");
        }
    } else {
        JOptionPane.showMessageDialog(this, "Vui lòng chọn một dòng trong bảng để sửa!");
    }
    }//GEN-LAST:event_jButton10ActionPerformed

    private void jButton12ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton12ActionPerformed
        String searchID = txtID.getText().trim();

        if (!searchID.isEmpty()) {
            connect db = new connect();
            Connection conn = null;
            PreparedStatement pst = null;
            ResultSet rs = null;

            try {
                conn = db.getConnection();
                if (conn == null) {
                    JOptionPane.showMessageDialog(this, "Không thể kết nối tới cơ sở dữ liệu.");
                    return;
                }

                DefaultTableModel model = (DefaultTableModel) table.getModel();
                model.setRowCount(0); // Xóa tất cả các dòng hiện tại

                // Lấy dữ liệu từ cơ sở dữ liệu
                String sql = "SELECT * FROM tblStudent WHERE id = ?";
                pst = conn.prepareStatement(sql);
                pst.setString(1, searchID);
                rs = pst.executeQuery();

                while (rs.next()) {
                    String dob = rs.getString("dob");
                    if (!isValidDate(dob)) {
                        JOptionPane.showMessageDialog(this, "Ngày sinh không hợp lệ trong dữ liệu của sinh viên với ID: " + searchID);
                        return;
                    }

                    model.addRow(new Object[]{
                        model.getRowCount() + 1,
                        rs.getString("id"),
                        rs.getString("name"),
                        dob,
                        rs.getString("address"),
                        rs.getString("phone"),
                        rs.getString("mark")
                    });
                }

                if (model.getRowCount() == 0) {
                    JOptionPane.showMessageDialog(this, "Không tìm thấy sinh viên với ID: " + searchID);
                }

            } catch (SQLException e) {
                e.printStackTrace();
                JOptionPane.showMessageDialog(this, "Lỗi khi tìm kiếm dữ liệu: " + e.getMessage());
            } finally {
                try {
                    if (rs != null) {
                        rs.close();
                    }
                    if (pst != null) {
                        pst.close();
                    }
                    if (conn != null) {
                        conn.close();
                    }
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        } else {
            JOptionPane.showMessageDialog(this, "Vui lòng nhập ID sinh viên.");
        }


    }//GEN-LAST:event_jButton12ActionPerformed

    public static void main(String args[]) {
        /* Set the Nimbus look and feel */
        //<editor-fold defaultstate="collapsed" desc=" Look and feel setting code (optional) ">
        /* If Nimbus (introduced in Java SE 6) is not available, stay with the default look and feel.
         * For details see http://download.oracle.com/javase/tutorial/uiswing/lookandfeel/plaf.html 
         */
        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (ClassNotFoundException ex) {
            java.util.logging.Logger.getLogger(frame.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(frame.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(frame.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(frame.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new frame().setVisible(true);
            }
        });
    }

    //Khai bao cac thanh phan cua giao dien
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton jButton1;
    private javax.swing.JButton jButton10;
    private javax.swing.JButton jButton11;
    private javax.swing.JButton jButton12;
    private javax.swing.JButton jButton14;
    private javax.swing.JButton jButton3;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel13;
    private javax.swing.JLabel jLabel14;
    private javax.swing.JLabel jLabel15;
    private javax.swing.JLabel jLabel16;
    private javax.swing.JLabel jLabel17;
    private javax.swing.JLabel jLabel18;
    private javax.swing.JLabel jLabel19;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel20;
    private javax.swing.JPanel jPanel7;
    private javax.swing.JPanel jPanel8;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JTable table;
    private javax.swing.JTextField txtAddress;
    private javax.swing.JTextField txtDob;
    private javax.swing.JTextField txtID;
    private javax.swing.JTextField txtMark;
    private javax.swing.JTextField txtName;
    private javax.swing.JTextField txtPhone;
    // End of variables declaration//GEN-END:variables

}
