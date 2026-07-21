import sys
from PySide6.QtWidgets import (
	QApplication, QCheckBox, QComboBox, 
	QFrame, QLabel, QMainWindow, QPushButton, QWidget,
	QGridLayout, QHBoxLayout, QVBoxLayout, QSizePolicy
	)

from PySide6.QtGui import (
	QPalette, QColor
	)

from PySide6.QtCore import (
	QTimer
)

BOARD_SIZE = 8

class Static_Button(QPushButton):
	"""
	Because hotkeys still triggered standard buttons 
	when they were not meant to
	"""
	def __init__(self, text):
		super().__init__(text)
	
	def keyPressEvent(self, event):
		event.ignore()


class Board(QWidget):
	def __init__(self):
		super().__init__()
		grid = QGridLayout(self)
		grid.setSpacing(0)
		for row in range(BOARD_SIZE):
			for col in range(BOARD_SIZE):
				square = QWidget()
				square.setSizePolicy(QSizePolicy.Policy.Expanding, QSizePolicy.Policy.Expanding)
				grid.addWidget(square, row, col)
				square.setAutoFillBackground(True)
		
				palette = square.palette()
				color = QColor("#0A0A0A") if (row+col) % 2 else QColor("#F0F0F0")
				palette.setColor(QPalette.ColorRole.Window, color)
				square.setPalette(palette)

		for i in range(BOARD_SIZE):
			grid.setRowStretch(i, 1)
			grid.setColumnStretch(i, 1)

	def resizeEvent(self, event):
		side = min(event.size().width(), event.size().height())
		self.resize(side, side)
		super().resizeEvent(event)
		

class MainWindow(QMainWindow):
	def __init__(self, window_title):
		super().__init__()
		
		self.setWindowTitle(window_title)
		self.resize(800,600)

		self.settings_widget = QWidget()
		self.game_frame = QFrame()

		self.init_settings_widget()
		self.init_game_frame()
		self.startup()

		central = QWidget()
		central_layout = QVBoxLayout(central)
		central_layout.addWidget(self.settings_widget)
		central_layout.addWidget(self.game_frame)
		self.setCentralWidget(central)

		self.startup()

	def init_settings_widget(self):
		settings_layout = QVBoxLayout(self.settings_widget)
		settings_layout.addWidget(QLabel("Ajustes"))

		play_button = Static_Button("Jugar")
		play_button.clicked.connect(self.show_game)
		settings_layout.addWidget(play_button)

	def init_game_frame(self):
		game_layout = QVBoxLayout(self.game_frame)

		self.board = Board()
		board_row = QHBoxLayout()
		board_row.addStretch(1)
		board_row.addWidget(self.board,6)
		board_row.addStretch(1)
		game_layout.addLayout(board_row)

		controls_layout = QHBoxLayout()

		restart_button = Static_Button("Reiniciar")
		restart_button.clicked.connect(self.restart_game)

		back_button = Static_Button("Ajustes")
		back_button.clicked.connect(self.show_settings)

		controls_layout.addWidget(restart_button)
		controls_layout.addWidget(back_button)
		game_layout.addLayout(controls_layout)

	def restart_game(self):
		old_board = self.board
		self.board = Board()
		self.game_frame.layout().replaceWidget(old_board, self.board)
		old_board.deleteLater()

	def show_settings(self):
		self.game_frame.hide()
		self.settings_widget.show()

	def show_game(self):
		print("game_frame parent:", self.game_frame.parent())
		self.settings_widget.hide()
		self.game_frame.show()
		print("board size:", self.board.size())
		print("board sizeHint:", self.board.sizeHint())
		print("board isVisible:", self.board.isVisible())

	def startup(self):
		self.game_frame.hide()


if __name__=='__main__':
	app = QApplication(sys.argv)
	
	window = MainWindow('Damas')
	window.show()

	sys.exit(app.exec())

