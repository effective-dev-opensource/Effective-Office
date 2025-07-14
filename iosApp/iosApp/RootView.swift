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
    let root: RootComponent

    func makeUIViewController(context: Context) -> UIViewController {
        return MainKt.rootViewController(root: root)
    }

    func updateUIViewController(_ uiViewController: UIViewController, context: Context) {
    }
}
