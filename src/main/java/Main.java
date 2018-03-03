import java.io.IOException;
import java.io.PrintWriter;
import java.io.Writer;
import javax.inject.Inject;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import services.GetOutService;

@WebServlet("/nuggets")
public class Main extends HttpServlet {

    @Inject
    private GetOutService getOutService;

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        resp.setContentType("application/json");
        PrintWriter writer = resp.getWriter();
//        writer.println("{'Hello':'World'}");
        writer.println(getOutService.sayGetOut("bro"));
        writer.close();
    }
}