module.exports = function (grunt) {
    grunt.initConfig({
        shell: { 
    		server: { 
                options: {
                    stdout: true,
                    stderr: true
                },
     	        command: 'java -Dlog4j.configurationFile=log4j2.xml -jar blackJack-1.0-jar-with-dependencies.jar'
    		}
        },
		fest: {
			templates: { /* Подзадача */
                files: [{
                    expand: true,
                    cwd: 'templates', /* исходная директория */
                    src: ['**/*.xml'], /* имена шаблонов */
                    dest: 'public_html/js/tmpl' /* результирующая директория */
                }], 
        	    options: {
                	template: function (data) {
                        return grunt.template.process(
                            // 'var <%= name %>Tmpl = <%= contents %> ;',
                            'define(function () { return <%= contents %> ; });',
                            {data: data}
                        );
                    }
        	    }
	       }		
		},
	    watch: {
            fest: {
                files: ['templates/**/*.xml'],
                tasks: ['fest'],
                options: {
                    interrupt: true,
                    atBegin: true
                }
            },
            server: {
                files: [
                    'public_html/js/**/*.js',
                    'public_html/css/**/*.css'
                ],
                options: {
                    livereload: true
                }
            },
            sass: {
                files: ['public_html/css/scss/*.scss'],
                tasks: ['sass'],
                options: {
                    atBegin: true,
                    interrupt: true
                }
            }
        },
        concurrent: {
            target: ['watch', 'shell'],
            options: {
                logConcurrentOutput: true
            }
        },
        sass: {
            dist: {
                files: [{
                    expand: true,
                    cwd: 'public_html/css/scss',
                    src: '*.scss',
                    dest: 'public_html/css',
                    ext: '.css'
                }]
            }
        },
        requirejs: {
            build: { /* Подзадача */
              options: {
                almond: true,
                baseUrl: "public_html/js",
                mainConfigFile: "public_html/js/main.js",
                name: "main",
                optimize: "none",
                out: "public_html/js/build/main.js"
              } }
        },
        concat: {
            build: { /* Подзадача */
                separator: ';\n',
                src: [
                      'public_html/js/lib/almond.js',
                      'public_html/js/build/main.js'
                ],
                dest: 'public_html/js/build.js'
            }
        },
        uglify: {
            build: { /* Подзадача */
                files: {
                    'public_html/js/build.min.js':
                          ['public_html/js/build.js']
                }
            }
        }
    });

    grunt.loadNpmTasks('grunt-contrib-watch');
    grunt.loadNpmTasks('grunt-concurrent');
    grunt.loadNpmTasks('grunt-shell');
    grunt.loadNpmTasks('grunt-fest');
    grunt.loadNpmTasks('grunt-sass');
    grunt.loadNpmTasks('grunt-contrib-requirejs');
    grunt.loadNpmTasks('grunt-contrib-concat');
    grunt.loadNpmTasks('grunt-contrib-uglify');

    grunt.registerTask('default', ['concurrent']);
    grunt.registerTask('build', ['requirejs', 'concat', 'uglify']);
//    grunt.registerTask('default', 'concat vs. uglify', function (concat) {
//        // grunt default:true
//        if (concat) {
//            // Update the uglify dest to be the result of concat
//            var dest = grunt.config('concat.js.dest');
//            grunt.config('uglify.target.src', dest);
//
//            grunt.task.run('concat');
//        }
//
//        // grunt default
//        grunt.task.run('uglify');
//    });

};