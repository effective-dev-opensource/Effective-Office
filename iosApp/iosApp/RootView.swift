//
//  RootView.swift
//  iosApp
//
//  Created by Stanislav Radchenko on 04.07.2025.
//
import ComposeApp
import Foundation
import SwiftUI

struct RootView: UIViewControllerRepresentable {
    func makeUIViewController(context: Context) -> UIViewController {
        return MainKt.rootViewController()
    }

    func updateUIViewController(_ uiViewController: UIViewController, context: Context) {
    }
}
