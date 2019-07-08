import java.awt.Button;
import java.awt.Frame;
import java.awt.Panel;
import java.awt.TextArea;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;

public class client_RCP extends Frame implements ActionListener {

	private TextArea msgView = new TextArea();
	private Button scissor, rock, paper;
	private DataInputStream reader;
	private DataOutputStream writer;

	public static int R = 0;
	public static int S = 1;
	public static int P = 2;

	Socket socket;

	public client_RCP(String title) { // 생성자

		super(title);

		msgView.setEditable(false);

		// 필요한 버튼 객체를 생성하고 배치한다.

		rock = new Button("ROCK");
		scissor = new Button("SCISSOR");
		paper = new Button("PAPER");

		add(msgView, "Center");

		Panel p = new Panel();

		p.add(rock);
		p.add(scissor);
		p.add(paper);

		add(p, "South");

		// 버튼들의 이벤트를 처리한다.

		rock.addActionListener(this);
		scissor.addActionListener(this);
		paper.addActionListener(this);

		pack();

	}

	private void connect() {

		try {

			msgView.append("CONNECTING WITH SERVER.....\n");
			socket = new Socket("211.63.89.73", 7777);
			msgView.append("*****GAME START*****\n");

			// 소켓의 입·출력 스트림을 얻는다.
			reader = new DataInputStream(socket.getInputStream());
			writer = new DataOutputStream(socket.getOutputStream());

		} catch (Exception e) {
			msgView.append("연결 Denied");
		}

	}

	public void actionPerformed(ActionEvent ae) { // 액션 이벤트 처리

		int player = -1, server = -1; // -1은 선택되지 않은 상태

		// 사용자가 누른 버튼에 해당하는 값을 기억
		if (ae.getSource() == rock)
			player = R;

		else if (ae.getSource() == scissor)
			player = S;

		else if (ae.getSource() == paper)
			player = P;

		// 다른 컨트롤에서 발생한 이벤트이면 메소드를 빠져 나온다.
		if (player == -1)
			return;
		try {
			// "OK"를 서버로 전송
			writer.writeUTF("OK");
			writer.flush();

			// 서버의 응답을 얻는다. 0~2 사이의 정수
			server = reader.readInt();

		} catch (IOException ie) {
		}

		// 승부 결과를 계산하여 msgView에 나타낸다.
		if (player == server) {
			msgView.append("상대방도 같은 수를 냈습니다.");
			msgView.append("SAME SAME.\n");
		} else if (player > server || server - player == 2)
			msgView.append("YOU WIN.\n");
		else {
			msgView.append("YOU LOSE.\n");
		}

	}

	public static void main(String[] args) {

		client_RCP client = new client_RCP("RCP GAME");
		client.setVisible(true);
		client.connect();

	}

}