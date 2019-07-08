import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Random;
import java.util.Vector;

public class server_RCP {

	private ServerSocket server;
	Vector scontrol = new Vector();

	Random rnd = new Random(); // 난수를 생성

	public server_RCP() {

	}

	void startServer() {
		try {

			server = new ServerSocket(7777);

			while (true) {

				Socket socket = server.accept();
				System.out.println("클라이언트와 연결 성공!");

				// 클라이언트와 통신하는 스레드를 생성하고 실행시킨다.
				new RCPThread(socket).start();

				// 소켓 관리자 리스트에 소켓을 추가한다.
				scontrol.add(socket);

				// 현재 접속하고 있는 클라이언트의 수를 화면에 출력한다.
				System.out.println("(현) 클라이언트 수: " + scontrol.size());

			}

		} catch (Exception e) {
			System.out.println(e);

		}

	}

	public static void main(String[] args) {

		server_RCP server = new server_RCP();
		server.startServer(); // 서버를 실행한다.

	}

	// 클라이언트와 통신하는 스레드 클래스

	class RCPThread extends Thread {

		Socket socket; // 소켓의 레퍼런스
		private DataInputStream reader; // 소켓의 입력 스트림
		private DataOutputStream writer; // 소켓의 출력 스트림

		RCPThread(Socket socket) { // 생성자
			this.socket = socket;

		}

		public void run() {
			try {

				// 소켓의 입·출력 스트림
				reader = new DataInputStream(socket.getInputStream());
				writer = new DataOutputStream(socket.getOutputStream());
				String msg;

				// 입력 스트림으로부터 메시지 얻기
				while ((msg = reader.readUTF()) != null) {
					if (msg.equals("OK")) { // 받은 메시지가 "OK"이면

						// 0에서 2사이의 정수형 난수를 클라이언트로 전송
						writer.writeInt(rnd.nextInt(3));
						writer.flush();

					}

				}

			} catch (Exception e) {
				e.printStackTrace();
			} finally { // 클라이언트와 접속이 끊겼을 때의 처리

				try {

					scontrol.remove(socket); // 소켓 관리자 리스트에서 소켓을 제거

					// 입·출력 스트림과 소켓을 닫는다.
					if (reader != null)
						reader.close();

					if (writer != null)
						writer.close();

					if (socket != null)
						socket.close();

					reader = null;
					writer = null;
					socket = null;

					// 적당한 정보를 화면에 출력한다.

					System.out.println("클라이언트가 종료했습니다...");
					System.out.println("(현) 클라이언트 수: " + scontrol.size());

				} catch (Exception e) {
				}

			}

		}

	}

}