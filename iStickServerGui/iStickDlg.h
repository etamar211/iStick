
// iStickDlg.h : header file
//

#pragma once


// CiStickDlg dialog
class CiStickDlg : public CDialogEx
{
// Construction
public:
	CiStickDlg(CWnd* pParent = NULL);	// standard constructor

// Dialog Data
	enum { IDD = IDD_ISTICK_DIALOG };

	protected:
	virtual void DoDataExchange(CDataExchange* pDX);	// DDX/DDV support


// Implementation
protected:
	HICON m_hIcon;

	// Generated message map functions
	virtual BOOL OnInitDialog();
	afx_msg void OnSysCommand(UINT nID, LPARAM lParam);
	afx_msg void OnPaint();
	afx_msg HCURSOR OnQueryDragIcon();
	DECLARE_MESSAGE_MAP()
	afx_msg void OnClose();
public:
	afx_msg void OnBnClickedButton1();
	afx_msg void OnBnClickedButton2();
};
