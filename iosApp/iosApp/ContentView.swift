import UIKit
import SwiftUI
import ComposeApp

struct ComposeView: UIViewControllerRepresentable {
    func makeUIViewController(context: Context) -> UIViewController {
        MainViewControllerKt.MainViewController()
    }

    func updateUIViewController(_ uiViewController: UIViewController, context: Context) {}
}


struct ContentView: View {
    @State private var isShowingCompose = false
    var body: some View {
        if isShowingCompose {
            ComposeView()
                .ignoresSafeArea(.keyboard) // Compose has own keyboard handler
        } else {
            Button("Show Compose") {
                isShowingCompose = true
            }
        }
    }
}



