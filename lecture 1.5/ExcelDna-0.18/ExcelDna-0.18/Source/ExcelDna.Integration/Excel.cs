/*
  Copyright (C) 2005-2008 Govert van Drimmelen

  This software is provided 'as-is', without any express or implied
  warranty.  In no event will the authors be held liable for any damages
  arising from the use of this software.

  Permission is granted to anyone to use this software for any purpose,
  including commercial applications, and to alter it and redistribute it
  freely, subject to the following restrictions:

  1. The origin of this software must not be misrepresented; you must not
     claim that you wrote the original software. If you use this software
     in a product, an acknowledgment in the product documentation would be
     appreciated but is not required.
  2. Altered source versions must be plainly marked as such, and must not be
     misrepresented as being the original software.
  3. This notice may not be removed or altered from any source distribution.


  Govert van Drimmelen
  govert@icon.co.za
*/

using System;
using System.Collections.Generic;
using System.Runtime.InteropServices;
using System.Text;

namespace ExcelDna.Integration
{
    [Obsolete("Use ExcelDna.Integration.ExcelDnaUtil class.")]
    public class Excel
    {
        [Obsolete("Use ExcelDna.Integration.ExcelDnaUtil.WindowHandle property.")]
        public static IntPtr WindowHandle
        {
            get { return ExcelDnaUtil.WindowHandle; }
        }

        [Obsolete("Use ExcelDna.Integration.ExcelDnaUtil.Application property.")]
        public static object Application
        {
            get { return ExcelDnaUtil.Application; }
        }

        [Obsolete("Use ExcelDna.Integration.ExcelDnaUtil.IsInFunctionWizard property.")]
        public static bool IsInFunctionWizard()
        {
            return ExcelDnaUtil.IsInFunctionWizard();
        }
    }

	public class ExcelDnaUtil
	{
		private delegate bool EnumWindowsCallback(IntPtr hwnd, /*ref*/ IntPtr param);

		[DllImport("user32.dll")]
		private static extern int EnumWindows(EnumWindowsCallback callback, /*ref*/ IntPtr param);
		[DllImport("user32.dll")]
		private static extern IntPtr GetParent(IntPtr hwnd);
		[DllImport("user32.dll")]
		private static extern bool EnumChildWindows(IntPtr hWndParent, EnumWindowsCallback callback, /*ref*/ IntPtr param);
		[DllImport("user32.dll")]
		private static extern int GetClassNameW(IntPtr hwnd, [MarshalAs(UnmanagedType.LPWStr)] StringBuilder buf, int nMaxCount);
        [DllImport("user32.dll")]
        private static extern IntPtr GetWindowTextW(IntPtr hwnd, [MarshalAs(UnmanagedType.LPWStr)] StringBuilder buf, int nMaxCount);
        [DllImport("Oleacc.dll")]
		private static extern int AccessibleObjectFromWindow(
			  IntPtr hwnd, uint dwObjectID, byte[] riid,
			  ref IntPtr ptr /*ppUnk*/);

		private static IntPtr _hWndExcel = IntPtr.Zero;
		public static IntPtr WindowHandle
		{
			get
			{
				// CONSIDER: Process.GetCurrentProcess().MainWindowHandle;
				if (_hWndExcel == IntPtr.Zero)
				{
                    if (ExcelDnaUtil.ExcelVersion < 12)
                    {
                        // Only have the loword so far.
                        ushort loWord = (ushort)(double)XlCall.Excel(XlCall.xlGetHwnd);
                        EnumWindows(delegate(IntPtr hWndEnum, IntPtr param)
                            {
                                // Check the loWord
                                if (((uint)hWndEnum & 0x0000FFFF) == (uint)loWord)
                                {
                                    // Check the window class
                                    StringBuilder cname = new StringBuilder(256);
                                    GetClassNameW(hWndEnum, cname, cname.Capacity);
                                    if (cname.ToString() == "XLMAIN")
                                    {
                                        _hWndExcel = hWndEnum;
                                        return false;	// Stop enumerating
                                    }
                                }
                                return true;	// Continue enumerating
                            }, (IntPtr)0);
                    }
                    else
                    {
                        _hWndExcel = (IntPtr)(int)(double)XlCall.Excel(XlCall.xlGetHwnd);
                    }
				}
				return _hWndExcel;
			}
		}

		private static object _Application = null;
		public static object Application
		{
			get
			{
				if (_Application == null)
				{
					_Application = GetApplicationFromWindow();
					if (_Application == null)
					{
						// I assume it failed because there was no workbook open
						// Now make workbook with VBA sheet, according to some Google post

                        // TODO: Consider alternative of sending WM_USER+18 to Excel - KB 147573

						// Create new workbook with the right stuff
						XlCall.Excel(XlCall.xlcEcho, false);
						XlCall.Excel(XlCall.xlcNew, 5);
						XlCall.Excel(XlCall.xlcWorkbookInsert, 6);

						_Application = GetApplicationFromWindow();

						// Clean up
						XlCall.Excel(XlCall.xlcFileClose, false);
						XlCall.Excel(XlCall.xlcEcho, true);
					}
				}
				return _Application;
			}
		}

		private static object GetApplicationFromWindow()
		{
			// This is Andrew Whitechapel's plan for getting the Application object.
			// It does not work when there are no Workbooks open.
			IntPtr hWndMain = WindowHandle;
			IntPtr hWndChild = IntPtr.Zero;
			EnumChildWindows(hWndMain, delegate(IntPtr hWndEnum, IntPtr param)
				{
					// Check the window class
					StringBuilder cname = new StringBuilder(256);
					GetClassNameW(hWndEnum, cname, cname.Capacity);
					if (cname.ToString() == "EXCEL7")
					{
						hWndChild = hWndEnum;
						return false;	// Stop enumerating
					}
					return true;	// Continue enumerating
				} , (IntPtr)0);
			if (hWndChild != (IntPtr)0)
			{
				const uint OBJID_NATIVEOM = 0xFFFFFFF0;
				Guid IID_IDispatch = new Guid(
					 "{00020400-0000-0000-C000-000000000046}");
				IntPtr ptr = (IntPtr)0;
				int hr = AccessibleObjectFromWindow(
						hWndChild, OBJID_NATIVEOM,
						IID_IDispatch.ToByteArray(), ref ptr);
				if (hr >= 0)
				{
					object obj = Marshal.GetObjectForIUnknown(ptr);
					object app = obj.GetType().InvokeMember("Application", System.Reflection.BindingFlags.GetProperty, null, obj, null);
					Marshal.ReleaseComObject(obj);
					//							object ver = app.GetType().InvokeMember("Version", System.Reflection.BindingFlags.GetProperty, null, app, null);
					return app;
				}
			}
			return null;
		}

		public static bool IsInFunctionWizard()
		{
            // TODO: Handle the Find and Replace dialog
            //       for international versions.
			IntPtr hWndMain = WindowHandle;
			bool inFunctionWizard = false;
			EnumWindows(delegate(IntPtr hWndEnum, IntPtr param)
				{
					// Check the window class
					StringBuilder cname = new StringBuilder(256);
					GetClassNameW(hWndEnum, cname, cname.Capacity);
					if (cname.ToString().StartsWith("bosa_sdm_XL"))
					{
                        if (GetParent(hWndEnum) == hWndMain)
                        {
                            StringBuilder title = new StringBuilder(256);
                            GetWindowTextW(hWndEnum, title, title.Capacity);
                            if (!title.ToString().Contains("Replace"))
                                inFunctionWizard = true; // will also work for older verions where past box had no title
                            return false;	// Stop enumerating
                        }
					}
					return true;	// Continue enumerating
				} , (IntPtr)0);
			return inFunctionWizard;
		}

        private static double _xlVersion = 0;
        public static double ExcelVersion
        {
            get
            {
                if (_xlVersion == 0)
                {
                    object versionString;
                    versionString = XlCall.Excel(XlCall.xlfGetWorkspace, 2);
                    double version;
                    bool parseOK = double.TryParse((string)versionString, out version);
                    if (!parseOK)
                    {
                        // Might be locale problem 
                        // and Excel 12 returns versionString with "." as decimal sep.
                        //  ->  microsoft.public.excel.sdk thread
                        //      Excel4(xlfGetWorkspace, &version, 1, & arg) - Excel2007 Options 
                        //      Dec 12, 2006
                        parseOK = double.TryParse((string)versionString,
                                    System.Globalization.NumberStyles.AllowDecimalPoint,
                                    System.Globalization.NumberFormatInfo.InvariantInfo,
                                    out version);
                    }
                    if (!parseOK)
                    {
                        version = 0.99;
                    }
                    _xlVersion = version;
                }
                return _xlVersion;
            }
        }

        private static ExcelLimits _xlLimits;
        public static ExcelLimits ExcelLimits
        {
            get
            {
                if (_xlLimits == null)
                {
                    _xlLimits = new ExcelLimits();
                    if (ExcelVersion < 12.0)
                    {
                        _xlLimits.MaxRows = 65536;
                        _xlLimits.MaxColumns = 256;
                        _xlLimits.MaxArguments = 30;
                        _xlLimits.MaxStringLength = 255;
                    }
                    else
                    {
                        _xlLimits.MaxRows = 1048576;
                        _xlLimits.MaxColumns = 16384;
                        _xlLimits.MaxArguments = 255;
                        _xlLimits.MaxStringLength = 32767;
                    }
                }
                return _xlLimits;
            }
        }
	}

    public class ExcelLimits
    {
        public int MaxRows { get; internal set; }
        public int MaxColumns { get; internal set; }
        public int MaxArguments { get; internal set; }
        public int MaxStringLength { get; internal set; }
    }

}
