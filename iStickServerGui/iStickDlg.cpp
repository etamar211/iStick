
// iStickDlg.cpp : implementation file
//

#include "stdafx.h"
#include "iStick.h"
#include "iStickDlg.h"
#include "afxdialogex.h"
#include "Resource.h"
#include <afxstr.h>
#include <atlimage.h>
#include <thread>
#include "C:\Users\RANANIM\Documents\Visual Studio 2013\Projects\iStick\istickserver\iStickServer\iStickServer\main.cpp"
#include "C:\Users\RANANIM\Documents\Visual Studio 2013\Projects\iStick\istickserver\iStickServer\iStickServer\BarcodeCreator.h"
#include "C:\Users\RANANIM\Documents\Visual Studio 2013\Projects\iStick\istickserver\iStickServer\iStickServer\BarcodeCreator.cpp"
#include "C:\Users\RANANIM\Documents\Visual Studio 2013\Projects\iStick\istickserver\iStickServer\iStickServer\ServerLogic.h"
#include "C:\Users\RANANIM\Documents\Visual Studio 2013\Projects\iStick\istickserver\iStickServer\iStickServer\ServerLogic.cpp"
#include "C:\Users\RANANIM\Documents\Visual Studio 2013\Projects\iStick\istickserver\iStickServer\iStickServer\keyboardAction.h"
#include "C:\Users\RANANIM\Documents\Visual Studio 2013\Projects\iStick\istickserver\iStickServer\iStickServer\keyboardAction.cpp"
#include "C:\Users\RANANIM\Documents\Visual Studio 2013\Projects\iStick\istickserver\iStickServer\iStickServer\mouseAction.h"
#include "C:\Users\RANANIM\Documents\Visual Studio 2013\Projects\iStick\istickserver\iStickServer\iStickServer\mouseAction.cpp"
#include "QRCreator\qrgenerator.cpp"
#ifdef _DEBUG
#define new DEBUG_NEW
#endif

void barcodeCheck(HWND);
static int counter = 0;

// CAboutDlg dialog used for App About

class CAboutDlg : public CDialogEx
{
public:
	CAboutDlg();

// Dialog Data
	enum { IDD = IDD_ABOUTBOX };

	protected:
	virtual void DoDataExchange(CDataExchange* pDX);    // DDX/DDV support

// Implementation
protected:
	DECLARE_MESSAGE_MAP()
};

CAboutDlg::CAboutDlg() : CDialogEx(CAboutDlg::IDD)
{
}

void CAboutDlg::DoDataExchange(CDataExchange* pDX)
{
	CDialogEx::DoDataExchange(pDX);
}

BEGIN_MESSAGE_MAP(CAboutDlg, CDialogEx)
END_MESSAGE_MAP()


// CiStickDlg dialog



CiStickDlg::CiStickDlg(CWnd* pParent /*=NULL*/)
	: CDialogEx(CiStickDlg::IDD, pParent)
{
	m_hIcon = AfxGetApp()->LoadIcon(IDI_ICON1);
}

void CiStickDlg::DoDataExchange(CDataExchange* pDX)
{
	CDialogEx::DoDataExchange(pDX);
}

BEGIN_MESSAGE_MAP(CiStickDlg, CDialogEx)
	ON_WM_SYSCOMMAND()
	ON_WM_PAINT()
	ON_WM_QUERYDRAGICON()
	ON_BN_CLICKED(IDC_BUTTON1, &CiStickDlg::OnBnClickedButton1)
	ON_COMMAND(IDCANCEL, &CiStickDlg::OnClose)
END_MESSAGE_MAP()


// CiStickDlg message handlers

BOOL CiStickDlg::OnInitDialog()
{
	CDialogEx::OnInitDialog();

	// Add "About..." menu item to system menu.
	pPCView = (CStatic *)GetDlgItem(IDOK);
	textView = (CStatic *)GetDlgItem(IDC_EDIT4);
	// IDM_ABOUTBOX must be in the system command range.
	ASSERT((IDM_ABOUTBOX & 0xFFF0) == IDM_ABOUTBOX);
	ASSERT(IDM_ABOUTBOX < 0xF000);

	CMenu* pSysMenu = GetSystemMenu(FALSE);
	if (pSysMenu != NULL)
	{
		BOOL bNameValid;
		CString strAboutMenu;
		bNameValid = strAboutMenu.LoadString(IDS_ABOUTBOX);
		ASSERT(bNameValid);
		if (!strAboutMenu.IsEmpty())
		{
			pSysMenu->AppendMenu(MF_SEPARATOR);
			pSysMenu->AppendMenu(MF_STRING, IDM_ABOUTBOX, strAboutMenu);
		}
	}

	// Set the icon for this dialog.  The framework does this automatically
	//  when the application's main window is not a dialog
	SetIcon(m_hIcon, TRUE);			// Set big icon
	SetIcon(m_hIcon, FALSE);		// Set small icon

	// TODO: Add extra initialization here

	return TRUE;  // return TRUE  unless you set the focus to a control
}

void CiStickDlg::OnSysCommand(UINT nID, LPARAM lParam)
{
	if ((nID & 0xFFF0) == IDM_ABOUTBOX)
	{
		CAboutDlg dlgAbout;
		dlgAbout.DoModal();
	}
	else
	{
		CDialogEx::OnSysCommand(nID, lParam);
	}
}

// If you add a minimize button to your dialog, you will need the code below
//  to draw the icon.  For MFC applications using the document/view model,
//  this is automatically done for you by the framework.

void CiStickDlg::OnPaint()
{

	if (IsIconic())
	{
		CPaintDC dc(this); // device context for painting

		SendMessage(WM_ICONERASEBKGND, reinterpret_cast<WPARAM>(dc.GetSafeHdc()), 0);

		// Center icon in client rectangle
		int cxIcon = GetSystemMetrics(SM_CXICON);
		int cyIcon = GetSystemMetrics(SM_CYICON);
		CRect rect;
		GetClientRect(&rect);
		int x = (rect.Width() - cxIcon + 1) / 2;
		int y = (rect.Height() - cyIcon + 1) / 2;

		// Draw the icon
		dc.DrawIcon(x, y, m_hIcon);
	}
	else
	{
		CDialogEx::OnPaint();
	}
}

// The system calls this function to obtain the cursor to display while the user drags
//  the minimized window.
HCURSOR CiStickDlg::OnQueryDragIcon()
{
	return static_cast<HCURSOR>(m_hIcon);
}



void CiStickDlg::OnBnClickedButton1()
{
	// TODO: Add your control notification handler code here
	if (counter < 1)
	{
		std::thread* t1 = new std::thread(main);
		std::thread* t2 = new std::thread(barcodeCheck, m_hWnd);
		t1->detach();
		t2->detach();
		counter++;
	}
	else
	{
		MessageBox(_T("Server is already running!"));
	}

}

void barcodeCheck(HWND m_hwnd)
{
	std::string currIp, currPass,tmpIP,tmpPass;
	CImage viewImage;
	CBitmap viewBitmap;
	size_t convertedChars = 24;
	wchar_t * wcstring = new wchar_t[24];
	currIp = BC->getIp();
	currPass = BC->getPass();
	mbstowcs_s(&convertedChars, wcstring, convertedChars, BC->getPass().c_str(), _TRUNCATE);
	SetDlgItemText(m_hwnd,IDC_EDIT3, wcstring);
	mbstowcs_s(&convertedChars, wcstring, convertedChars, BC->getIp().c_str(), _TRUNCATE);
	SetDlgItemText(m_hwnd,IDC_EDIT4, wcstring);
	viewImage.Load(_T("QRCode.jpg"));
	viewBitmap.Attach(viewImage.Detach());
	pPCView->SetBitmap((HBITMAP)viewBitmap);
	while (true)
	{
		tmpIP = BC->getIp();
		tmpPass = BC->getPass();
		if (tmpIP != currIp || tmpPass != currPass)
		{
			mbstowcs_s(&convertedChars, wcstring, convertedChars, BC->getPass().c_str(), _TRUNCATE);
			SetDlgItemText(m_hwnd, IDC_EDIT3, wcstring);
			mbstowcs_s(&convertedChars, wcstring, convertedChars, BC->getIp().c_str(), _TRUNCATE);
			SetDlgItemText(m_hwnd, IDC_EDIT4, wcstring);
			viewBitmap.Detach();
			viewImage.Load(_T("QRCode.jpg"));
			viewBitmap.Attach(viewImage.Detach());
			pPCView->SetBitmap((HBITMAP)viewBitmap);
			viewImage.Destroy();
			viewBitmap.DeleteObject();
		}
		Sleep(1000);
	}

}

afx_msg void CiStickDlg::OnClose()
{
	if ((GetKeyState(VK_ESCAPE) & 0x8000) == 0)
		OnCancel();
}


