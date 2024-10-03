{
	description = "nix build environment";
	
	inputs = {
		nixpkgs.url = "github:nixos/nixpkgs/nixpkgs-unstable";
	};

	outputs = { self, nixpkgs }:
		let
			universal = function:
				nixpkgs.lib.genAttrs [
					"x86_64-linux"
					"aarch64-linux"
				] (system: function nixpkgs.legacyPackages.${system});
		in {
			devShell = universal (pkgs: 
				(pkgs.mkShell rec {
					name = "monospace";

					libs = with pkgs; [
						freetype
						libGL
						xorg.libX11
						xorg.libXrandr
						xorg.libXcursor
						xorg.libXi
						xorg.libXinerama
						xorg.libXtst
						xorg.libXxf86vm
						libxkbcommon
						flac
						libvorbis
						openal
						udev
						glib
						fontconfig
						gtk3
						cairo
						pango
						harfbuzz
						atk
						gobject-introspection
						gdk-pixbuf
						wayland
						glfw
						stdenv.cc.cc.lib
					];

					LD_LIBRARY_PATH = pkgs.lib.makeLibraryPath libs;

					nativeBuildInputs = with pkgs; [
						graalvm-ce
						maven
					];

					buildInputs = with pkgs; [ libs ];
					
					shellHook = ''
						export JAVA_HOME="${pkgs.graalvm-ce}"
					'';
				}));
		};
}
