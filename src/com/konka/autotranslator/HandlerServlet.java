package com.konka.autotranslator;

import java.beans.PropertyVetoException;
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Properties;
import java.util.Random;

import javax.servlet.ServletException;
import javax.servlet.annotation.MultipartConfig;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.Part;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.json.JSONArray;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import com.mchange.v2.c3p0.ComboPooledDataSource;

@MultipartConfig(location = "/home/apache-tomcat/apache-tomcat-7.0.54/webapps/AutoTranslator/temp")
public class HandlerServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private static final String filePath = "/home/apache-tomcat/apache-tomcat-7.0.54/webapps/AutoTranslator/temp/";
	private static final String split = "&_&";
	private static final ComboPooledDataSource ds;
	private Random random = new Random();
	static {
		ds = new ComboPooledDataSource();
		try (FileInputStream fis = new FileInputStream("/home/apache-tomcat/apache-tomcat-7.0.54/webapps/AutoTranslator/database.properties");) {
			Properties p = new Properties();
			p.load(fis);
			ds.setDriverClass(p.getProperty("driverClass"));
			ds.setUser(p.getProperty("user"));
			ds.setPassword(p.getProperty("pwd"));
			ds.setJdbcUrl(p.getProperty("url"));
			ds.setMinPoolSize(Integer.parseInt(p.getProperty("minPoolSize")));
			ds.setMaxPoolSize(Integer.parseInt(p.getProperty("maxPoolSize")));
		}  catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (PropertyVetoException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public HandlerServlet() {
		super();
		try (Connection conn = ds.getConnection();
				PreparedStatement pstmt0 = conn.prepareStatement(
						"create table if not exists language(id int auto_increment primary key,language_name varchar(226) not null unique)");
				PreparedStatement pstmt1 = conn.prepareStatement(
						"create table if not exists custom(id int auto_increment primary key,custom_name varchar(226) not null unique)");
				PreparedStatement pstmt2 = conn.prepareStatement(
						"create table if not exists custom_language(id int auto_increment primary key,custom_id int not null,language_id int not null)");
				PreparedStatement pstmt3 = conn.prepareStatement(
						"create table if not exists entries(id int auto_increment primary key,entry_name blob not null)");) {
			
			pstmt0.executeUpdate();
			pstmt1.executeUpdate();
			pstmt2.executeUpdate();
			pstmt3.executeUpdate();

		} catch (SQLException e) {
			e.printStackTrace();
		}
		// TODO Auto-generated constructor stub
	}

	private void addLanguage(HttpServletRequest request, HttpServletResponse response) {
		try (Connection conn = ds.getConnection();
				PreparedStatement pstmt = conn.prepareStatement("insert into language values(null,?)");) {
			pstmt.setString(1, request.getParameter("language"));
			int result = pstmt.executeUpdate();
			if (result == 1) {
				response.getWriter().write("添加语言完成!");
			} else {
				response.getWriter().write("添加语言失败!");
			}
		} catch (SQLException | IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	private void queryLanguage(HttpServletRequest request, HttpServletResponse response) {
		try (Connection conn = ds.getConnection();
				PreparedStatement pstmt = conn.prepareStatement("select language_name from language");
				ResultSet rs = pstmt.executeQuery();) {
			String result = "";
			while (rs.next()) {
				result += rs.getString("language_name") + ",";
			}
			response.getWriter().write(result);
		} catch (SQLException | IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	private void addCustom(HttpServletRequest request, HttpServletResponse response) {
		String custom = request.getParameter("custom");
		String language = request.getParameter("language");
		String[] languages = language.split(" ");
		Connection conn = null;
		PreparedStatement pstmt0 = null;
		PreparedStatement pstmt1 = null;
		PreparedStatement pstmt2 = null;
		PreparedStatement pstmt3 = null;
		PreparedStatement pstmt4 = null;
		ResultSet rs0 = null;
		ResultSet rs1 = null;
		try {
			conn = ds.getConnection();
			String sql = "create table " + custom + "(id int auto_increment primary key,entry_id int not null unique,";
			for (int i = 0; i < languages.length - 1; i++) {
				sql += languages[i] + " text,";
			}
			sql += languages[languages.length - 1] + " text)";
			pstmt0 = conn.prepareStatement(sql);
			pstmt0.executeUpdate();
			pstmt0.close();
			pstmt1 = conn.prepareStatement("insert into custom values(null,?)");
			pstmt1.setString(1, custom);
			int result = pstmt1.executeUpdate();
			if (result == 1) {
				pstmt2 = conn.prepareStatement("select id from custom where custom_name=?");
				pstmt2.setString(1, custom);
				rs0 = pstmt2.executeQuery();
				int id = -1;
				if (rs0.next()) {
					id = rs0.getInt("id");
				}
				rs0.close();
				pstmt2.close();
				pstmt3 = conn.prepareStatement("select id from language where language_name=?");
				pstmt4 = conn.prepareStatement("insert into custom_language values(null," + id + ",?)");
				for (String temp : languages) {
					int languageId = -1;
					pstmt3.setString(1, temp);
					rs1 = pstmt3.executeQuery();
					if (rs1.next()) {
						languageId = rs1.getInt("id");
					}
					rs1.close();
					pstmt4.setInt(1, languageId);
					result = pstmt4.executeUpdate();
					if (result != 1) {
						response.getWriter().write("添加客户失败!");
						return;
					}
				}
				response.getWriter().write("添加客户成功!");

			} else {
				response.getWriter().write("添加客户失败!");
			}

		} catch (SQLException | IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			close(pstmt0);
			close(pstmt1);
			close(pstmt2);
			close(pstmt3);
			close(pstmt4);
			close(rs0);
			close(rs1);
		}
	}

	private void close(AutoCloseable closable) {
		if (closable != null) {
			try {
				closable.close();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	private void queryCustom(HttpServletRequest request, HttpServletResponse response) {
		ArrayList<Custom> result = new ArrayList<>();
		try (Connection conn = ds.getConnection();
				PreparedStatement pstmt = conn.prepareStatement(
						"select custom_name,language_name from custom,language,custom_language where custom.id=custom_language.custom_id and language.id=custom_language.language_id");
				ResultSet rs = pstmt.executeQuery();) {
			String custom_name = "";
			String temp = "";
			Custom custom = null;
			while (rs.next()) {
				custom_name = rs.getString("custom_name");
				if (!custom_name.equals(temp)) {
					temp = custom_name;
					custom = new Custom();
					result.add(custom);
					custom.setCustomName(custom_name);
				}
				custom.setLanguage(rs.getString("language_name"));
			}
			response.getWriter().print(new JSONArray(result));
		} catch (SQLException | IOException e) {
			e.printStackTrace();
		}
	}

	private String getFileName(Part part) {
		String header = part.getHeader("Content-Disposition");
		String fileName = header.substring(header.indexOf("filename=\"") + 10, header.lastIndexOf("\""));
		return fileName;
	}

	private String downloadFile(HttpServletRequest request) {
		String path = getNewDirectory();
		String fileName = null;
		try {
			Part part = request.getPart("fileUpload");
			fileName = getFileName(part);
			part.write(path + fileName);
		} catch (IOException | ServletException e) {
			e.printStackTrace();
		}
		return path + fileName;
	}

	private void handleComflict(String fileName, String customName, HashSet<Comflict> hashSet,
			HttpServletRequest request) {
		XSSFWorkbook workbook = new XSSFWorkbook();
		XSSFSheet sheet = workbook.createSheet("sheet1");
		XSSFRow row = sheet.createRow(0);
		XSSFCell cell = row.createCell((short) 0);
		cell.setCellValue("id");
		cell = row.createCell((short) 1);
		cell.setCellValue("sheet name");
		cell = row.createCell((short) 2);
		cell.setCellValue("tag name");
		cell = row.createCell((short) 3);
		cell.setCellValue("english");
		cell = row.createCell((short) 4);
		cell.setCellValue("language");
		cell = row.createCell((short) 5);
		cell.setCellValue("old value");
		cell = row.createCell((short) 6);
		cell.setCellValue("new value");
		int i = 1;
		for (Comflict comflict : hashSet) {
			row = sheet.createRow(i);
			cell = row.createCell((short) 0);
			cell.setCellValue("" + i++);
			for (int j = 1; j < 7; j++) {
				cell = row.createCell((short) j);
				cell.setCellValue(comflict.getName(j));
			}
		}
		try (FileOutputStream fos = new FileOutputStream(
				fileName.substring(0, fileName.lastIndexOf("/") + 1) + customName + ".xlsx");) {
			workbook.write(fos);
			workbook.close();
			request.getSession(true).setAttribute("fileName",
					fileName.substring(0, fileName.lastIndexOf("/") + 1) + customName + ".xlsx");
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	private synchronized String getNewDirectory() {
		File directory = new File(filePath + random.nextInt(1000));
		while (directory.exists()) {
			directory = new File(filePath + random.nextInt(1000));
		}
		directory.mkdir();
		return (directory.getAbsolutePath() + File.separator);
	}

	private void addData(HttpServletRequest request, HttpServletResponse response, boolean createXML) {

		HashSet<Comflict> comflicts = new HashSet<>();
		String customName = request.getParameter("customName");
		String fileName = downloadFile(request);

		String method = request.getParameter("method");
		String tagName = "";
		Connection conn = null;
		PreparedStatement pstmt0 = null;
		PreparedStatement pstmt1 = null;
		PreparedStatement pstmt2 = null;
		PreparedStatement pstmt3 = null;
		ResultSet rs = null;
		FileInputStream fio = null;
		XSSFWorkbook workbook = null;
		try {
			conn = ds.getConnection();
			HashSet<String> languages = new HashSet<>();
			pstmt0 = conn.prepareStatement(
					"select language_name from custom,language,custom_language where custom.id=custom_language.custom_id and language.id=custom_language.language_id and custom_name=?");
			pstmt0.setString(1, customName);
			rs = pstmt0.executeQuery();
			String sql = "";
			while (rs.next()) {
				languages.add(rs.getString("language_name"));
				sql += ",null";
			}
			rs.close();
			pstmt0.close();
			pstmt1 = conn.prepareStatement("select id from entries where entry_name=?");
			pstmt2 = conn.prepareStatement("insert into entries values(null,?)");
			pstmt3 = conn.prepareStatement("insert into " + customName + " values(null,?" + sql + ")");
			fio = new FileInputStream(fileName);
			workbook = new XSSFWorkbook(fio);
			Iterator<Sheet> iterator = workbook.iterator();
			while (iterator.hasNext()) {
				XSSFSheet sheet = (XSSFSheet) iterator.next();
				if (!workbook.isSheetHidden(workbook.getSheetIndex(sheet))) {
					HashMap<Integer, PreparedStatement[]> hashMap = new HashMap<>();
					int rowNums = sheet.getLastRowNum();
					XSSFRow row = sheet.getRow(0);
					int columns = row.getLastCellNum();
					HashSet<Integer> hashSetXML = new HashSet<>();
					for (int i = 2; i <= columns; i++) {
						XSSFCell cell = row.getCell(i);
						if (cell != null && cell.toString().length() > 0) {
							if (!languages.contains(cell.toString().trim())) {
								response.getWriter().write("excel中包含客户不支持的语言!!!");
								for (int index_local : hashMap.keySet()) {
									for (PreparedStatement stmt : hashMap.get(index_local)) {
										stmt.close();
									}
								}
								System.out.println("语言是:" + cell.toString());
								return;
							}
							if (createXML) {
								hashSetXML.add(i);
							}
							PreparedStatement[] stmtArray = new PreparedStatement[] {
									conn.prepareStatement("select " + cell.toString().trim() + " from " + customName
											+ " where entry_id=?"),
									conn.prepareStatement("update " + customName + " set " + cell.toString().trim()
											+ "=? where entry_id=?") };
							hashMap.put(i, stmtArray);
						}
					}

					if (hashMap.size() == 0) {
						response.getWriter().write("第一行没有添加语音!!!");
						return;
					}
					if (createXML) {
						readExcel(sheet, hashSetXML, fileName.substring(0, fileName.lastIndexOf("/") + 1));
					}
					for (int i = 1; i <= rowNums; i++) {

						row = sheet.getRow(i);
						if (row == null) {
							continue;
						}
						XSSFCell cell0 = row.getCell(0);
						if (cell0 != null && cell0.toString().length() > 0) {
							tagName = cell0.toString();
						}
						XSSFCell cell1 = row.getCell(1);
						if (cell1 == null || cell1.toString().length() == 0) {
							continue;
						}

						pstmt1.setString(1, cell1.toString());
						rs = pstmt1.executeQuery();
						int entry_id = -1;
						if (rs.next()) {
							entry_id = rs.getInt("id");
							rs.close();
							for (int index_local : hashMap.keySet()) {
								XSSFCell cell_local = row.getCell(index_local);
								if (cell_local == null || cell_local.toString().length() == 0) {
									continue;
								}
								hashMap.get(index_local)[0].setInt(1, entry_id);
								rs = hashMap.get(index_local)[0].executeQuery();
								if (rs.next()) {
									if (rs.getString(1) == null) {
										rs.close();
										hashMap.get(index_local)[1].setString(1, cell_local.toString());
										hashMap.get(index_local)[1].setInt(2, entry_id);
										int result = hashMap.get(index_local)[1].executeUpdate();
										if (result != 1) {
											response.getWriter().write("添加词条失败!!!");
											return;
										}
									} else if (rs.getString(1).equals(cell_local.toString())) {
										rs.close();
									} else {
										System.out.println("confilct");
										if (method.equals("create")) {
											Comflict comflict = new Comflict();
											comflict.setSheetName(sheet.getSheetName());
											comflict.setTagName(tagName);
											comflict.setEnglish(cell1.toString());
											comflict.setLanguage(sheet.getRow(0).getCell(index_local).toString());
											comflict.setOldTranslator(rs.getString(1));
											comflict.setNewTranslator(cell_local.toString());
											comflicts.add(comflict);
										} else {
											hashMap.get(index_local)[1].setString(1, cell_local.toString());
											hashMap.get(index_local)[1].setInt(2, entry_id);
											int result = hashMap.get(index_local)[1].executeUpdate();
											if (result != 1) {
												response.getWriter().write("添加词条失败!!!");
												return;
											}
										}
										rs.close();
									}
								} else {
									rs.close();
									pstmt3.setInt(1, entry_id);
									int result = pstmt3.executeUpdate();
									if (result != 1) {
										response.getWriter().write("添加词条失败!!!");
										return;
									}
									hashMap.get(index_local)[1].setString(1, cell_local.toString());
									hashMap.get(index_local)[1].setInt(2, entry_id);
									result = hashMap.get(index_local)[1].executeUpdate();
									if (result != 1) {
										response.getWriter().write("添加词条失败!!!");
										return;
									}
								}
							}
						} else {
							rs.close();
							pstmt2.setString(1, cell1.toString());
							int result = pstmt2.executeUpdate();
							if (result != 1) {
								response.getWriter().write("添加词条失败!!!");
								return;
							}
							pstmt1.setString(1, cell1.toString());
							rs = pstmt1.executeQuery();
							if (rs.next()) {
								entry_id = rs.getInt("id");
							}
							rs.close();
							pstmt3.setInt(1, entry_id);
							result = pstmt3.executeUpdate();
							if (result != 1) {
								response.getWriter().write("添加词条失败!!!");
								return;
							}
							for (int index_local : hashMap.keySet()) {
								XSSFCell cell_local = row.getCell(index_local);
								if (cell_local == null || cell_local.toString().length() == 0) {
									continue;
								}
								hashMap.get(index_local)[1].setString(1, cell_local.toString());
								hashMap.get(index_local)[1].setInt(2, entry_id);
								result = hashMap.get(index_local)[1].executeUpdate();
								if (result != 1) {
									response.getWriter().write("添加词条失败!!!");
									return;
								}
							}
						}
					}
					for (int index_local : hashMap.keySet()) {
						for (PreparedStatement stmt : hashMap.get(index_local)) {
							stmt.close();
						}
					}
					hashMap.clear();
				}
			}
		} catch (IOException | SQLException e) {
			e.printStackTrace();
		} finally {
			close(rs);
			close(pstmt0);
			close(pstmt1);
			close(pstmt2);
			close(pstmt3);
			close(conn);
			close(fio);
			close(workbook);
			File file = new File(fileName);
			file.delete();
			if (comflicts.size() != 0) {
				handleComflict(fileName, customName, comflicts, request);
				handlerComflictFinish(true, response);
			} else {
				handlerComflictFinish(false, response);
			}
			if (createXML) {
				try {
					String local_var = fileName.substring(0, fileName.lastIndexOf("/") + 1);
					Runtime.getRuntime()
							.exec(new String[] { "/bin/sh", "-c", "cd " + local_var + ";tar -czf out.tar.gz *" });
					request.getSession(true).setAttribute("fileName",
							fileName.substring(0, fileName.lastIndexOf("/") + 1) + "out.tar.gz");
				} catch (IOException e) {
					e.printStackTrace();
				}

			}

		}
	}

	private void handlerComflictFinish(boolean comflict, HttpServletResponse response) {
		try {
			if (comflict) {
				response.getWriter().write("comflict");
			} else {
				response.getWriter().write("处理完成");
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private void queryCustomName(HttpServletRequest request, HttpServletResponse response) {
		try (Connection conn = ds.getConnection();
				PreparedStatement pstmt = conn.prepareStatement("select * from custom");
				ResultSet rs = pstmt.executeQuery();) {
			String result = "";
			while (rs.next()) {
				result += rs.getString("custom_name") + ",";
			}
			response.getWriter().write(result);
		} catch (SQLException | IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	private void uploadFile(HttpServletRequest request, HttpServletResponse response) {
		String filedownload = (String) request.getSession().getAttribute("fileName");
		response.setContentType("application/x-download");
		response.addHeader("Content-Disposition",
				"attachment;filename=" + filedownload.substring(filedownload.lastIndexOf("/") + 1));
		File file = new File(filedownload);
		try (BufferedInputStream bis = new BufferedInputStream(new FileInputStream(file));
				BufferedOutputStream bos = new BufferedOutputStream(response.getOutputStream());) {
			byte[] b = new byte[4096];
			int len = 0;
			while ((len = bis.read(b)) != -1) {
				bos.write(b, 0, len);
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		File parent = file.getParentFile();
		File[] files = parent.listFiles();
		for (File temp : files) {
			temp.delete();
		}
		parent.delete();
	}

	private void fillExcel(HttpServletRequest request, HttpServletResponse response) {
		String fileName = downloadFile(request);
		String customName = request.getParameter("customName");
		HashSet<String> languages = new HashSet<>();
		XSSFWorkbook workbook = null;
		Connection conn = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		FileInputStream fio = null;
		FileOutputStream fos = null;
		try {
			conn = ds.getConnection();
			pstmt = conn.prepareStatement(
					"select language_name from custom,language,custom_language where custom.id=custom_language.custom_id and language.id=custom_language.language_id and custom_name=?");
			pstmt.setString(1, customName);
			rs = pstmt.executeQuery();
			while (rs.next()) {
				languages.add(rs.getString("language_name"));
			}
			rs.close();
			pstmt.close();

			fio = new FileInputStream(fileName);
			workbook = new XSSFWorkbook(fio);
			Iterator<Sheet> iterator = workbook.iterator();
			while (iterator.hasNext()) {
				XSSFSheet sheet = (XSSFSheet) iterator.next();
				if (!workbook.isSheetHidden(workbook.getSheetIndex(sheet))) {
					HashMap<Integer, PreparedStatement> hashMap = new HashMap<>();
					int rowNums = sheet.getLastRowNum();
					XSSFRow row = sheet.getRow(0);
					int columns = row.getLastCellNum();
					for (int i = 2; i <= columns; i++) {
						XSSFCell cell = row.getCell(i);

						if (cell != null && cell.toString().trim().length() > 0) {
							String translatorLanguage = cell.toString().trim();
							if (!languages.contains(translatorLanguage)) {
								response.getWriter().write("excel中包含客户不支持的语言!!!");
								for (int index_local : hashMap.keySet()) {
									hashMap.get(index_local).close();
								}
								return;
							}
							String sql = "select " + translatorLanguage + " from entries," + customName
									+ " where entries.id=" + customName + ".entry_id and entry_name=?";
							PreparedStatement stmt = conn.prepareStatement(sql);
							hashMap.put(i, stmt);
						}
					}
					if (hashMap.size() == 0) {
						response.getWriter().write("第一行没有添加语音!!!");
						return;
					}
					for (int i = 1; i <= rowNums; i++) {
						row = sheet.getRow(i);
						if (row == null) {
							continue;
						}
						XSSFCell cell1 = row.getCell(1);
						if (cell1 == null || cell1.toString().length() == 0) {
							continue;
						}
						for (int index_local : hashMap.keySet()) {
							hashMap.get(index_local).setString(1, cell1.toString());
							rs = hashMap.get(index_local).executeQuery();
							if (rs.next()) {
								XSSFCell cell_local = row.getCell(index_local);
								if (cell_local == null) {
									cell_local = row.createCell(index_local);
								}
								cell_local.setCellValue(rs.getString(1));
							}
							rs.close();
						}
					}
					for (int index_local : hashMap.keySet()) {
						hashMap.get(index_local).close();
					}
				}
			}
			fos = new FileOutputStream(fileName);
			workbook.write(fos);
			request.getSession(true).setAttribute("fileName", fileName);
			response.getWriter().write("补全Excel完成!!!");
		} catch (SQLException | IOException e) {
			e.printStackTrace();
		} finally {
			close(rs);
			close(pstmt);
			close(conn);
			close(fio);
			close(fos);
			close(workbook);
		}
	}

	private void createXML(HashSet<String> hashSet, String new_file_path) {
		try {
			DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
			DocumentBuilder builder = factory.newDocumentBuilder();
			Document document = builder.newDocument();
			document.setXmlStandalone(true);
			Element root = document.createElement("resources");
			Element father = null;
			for (String result : hashSet) {
				String[] key_value = result.split(split);
				if (key_value.length > 2) {
					father = document.createElement("string-array");
					father.setAttribute("name", key_value[0]);
					Element sub = null;
					for (int i = 1; i < key_value.length; i++) {
						sub = document.createElement("item");
						sub.setTextContent(key_value[i]);
						father.appendChild(sub);
					}
					root.appendChild(father);

				} else if (key_value.length == 2) {
					father = document.createElement("string");
					father.setAttribute("name", key_value[0]);
					father.setTextContent(key_value[1]);
					root.appendChild(father);
				}
			}
			document.appendChild(root);
			TransformerFactory tff = TransformerFactory.newInstance();
			Transformer tf = tff.newTransformer();
			tf.setOutputProperty(OutputKeys.INDENT, "yes");
			tf.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "4");
			tf.setOutputProperty(OutputKeys.METHOD, "xml");
			tf.transform(new DOMSource(document), new StreamResult(new File(new_file_path)));

		} catch (ParserConfigurationException | TransformerException e) {
			e.printStackTrace();
		}
	}

	private void readExcel(XSSFSheet sheet, HashSet<Integer> hashSet, String filePath) {
		int rowNums = sheet.getLastRowNum();
		for (int index : hashSet) {
			HashSet<String> result = new HashSet<>();
			String key = null;
			String value = null;
			for (int i = 1; i <= rowNums; i++) {
				XSSFRow row = sheet.getRow(i);
				if (row == null) {
					continue;
				}
				XSSFCell cell0 = row.getCell(0);
				XSSFCell celln = row.getCell(index);
				if (cell0 != null && cell0.toString().length() > 0 && celln != null && celln.toString().length() > 0) {
					if (value != null) {
						result.add(key + value);
						value = null;
					}
					result.add(cell0.toString() + split + celln.toString());
				} else if (cell0 != null && cell0.toString().length() > 0) {
					if (value != null) {
						result.add(key + value);
						value = null;
					}
					key = cell0.toString();
				} else if (celln != null && celln.toString().length() > 0) {
					if (value == null) {
						value = split;
					}
					value += celln.toString() + split;
				}
			}
			createXML(result,
					filePath + sheet.getSheetName() + "_" + sheet.getRow(0).getCell(index).toString() + ".xml");
		}
	}

	@Override
	public void service(HttpServletRequest request, HttpServletResponse response) {
		response.setCharacterEncoding("utf8");
		String cmd = request.getParameter("cmd");
		switch (cmd) {
		case "addLanguage":
			addLanguage(request, response);
			break;
		case "queryLanguage":
			queryLanguage(request, response);
			break;
		case "addCustom":
			addCustom(request, response);
			break;
		case "queryCustom":
			queryCustom(request, response);
			break;
		case "addData":
			addData(request, response, false);
			break;
		case "queryCustomName":
			queryCustomName(request, response);
			break;
		case "download":
			uploadFile(request, response);
			break;
		case "fillExcel":
			fillExcel(request, response);
			break;
		case "updateAndCreateXML":
			addData(request, response, true);
			break;
		}
	}
}
