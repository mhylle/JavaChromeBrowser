import org.cef.CefApp;
import org.cef.CefClient;
import org.cef.CefSettings;
import org.cef.OS;
import org.cef.browser.CefBrowser;
import org.cef.browser.CefMessageRouter;
import org.cef.browser.CefRequestContext;
import org.cef.handler.CefDisplayHandlerAdapter;
import org.cef.handler.CefRequestContextHandlerAdapter;
import org.cef.network.CefCookieManager;
import tests.detailed.dialog.DownloadDialog;
import tests.detailed.handler.*;

import javax.swing.*;
import java.awt.*;

/**
 * Created by mnh on 18-04-2017.
 */
public class ChromePanel
{

  public static void main(String[] args)
  {
    ChromePanel chromePanel = new ChromePanel();
    chromePanel.start();
  }

  private CefClient client_;
  private CefCookieManager cookieManager_;
  private CefBrowser browser_;
  private void start()
  {
    JFrame frame = new JFrame();
    frame.setSize(800,600);
    JPanel contentPanel = new JPanel(new BorderLayout());

    boolean osrEnabledArg = OS.isLinux();
    CefApp cefApp = setupBrowser(osrEnabledArg);

    client_ = cefApp.createClient();

    DownloadDialog downloadDialog = new DownloadDialog(frame);
    client_.addContextMenuHandler(new ContextMenuHandler(frame));
    client_.addDownloadHandler(downloadDialog);
    client_.addDragHandler(new DragHandler());
    client_.addGeolocationHandler(new GeolocationHandler(frame));
    client_.addJSDialogHandler(new JSDialogHandler());
    client_.addKeyboardHandler(new KeyboardHandler());
    client_.addRequestHandler(new RequestHandler(frame));

    CefMessageRouter msgRouter = CefMessageRouter.create();
    msgRouter.addHandler(new MessageRouterHandler(), true);
    msgRouter.addHandler(new MessageRouterHandlerEx(client_), false);
    client_.addMessageRouter(msgRouter);


    CefRequestContext requestContext = null;
    String cookiePath = "c:/temp/cookies";
    if (cookiePath != null) {
      cookieManager_ = CefCookieManager.createManager(cookiePath, false);
      requestContext = CefRequestContext.createContext(
          new CefRequestContextHandlerAdapter()
          {
            @Override
            public CefCookieManager getCookieManager()
            {
              return cookieManager_;
            }
          });
    } else {
      cookieManager_ = CefCookieManager.getGlobalManager();
    }
    browser_ = client_.createBrowser("http://localhost:4200",
        osrEnabledArg,
        false,
        requestContext);

    contentPanel.add(browser_.getUIComponent(),BorderLayout.CENTER);
    frame.getContentPane().add(contentPanel, BorderLayout.CENTER);
    frame.setVisible(true);

  }

  private CefApp setupBrowser(boolean osrEnabledArg)
  {
    CefSettings settings = new CefSettings();


    settings.windowless_rendering_enabled = false;
    // try to load URL "about:blank" to see the background color
    settings.background_color = settings.new ColorType(100, 255, 242, 211);
    CefApp myApp = CefApp.getInstance(null, settings);
    CefApp.CefVersion version = myApp.getVersion();
    System.out.println("version = " + version);
    return myApp;
  }
}
