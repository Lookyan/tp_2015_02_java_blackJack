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
        }
    });

    grunt.loadNpmTasks('grunt-contrib-watch');
    grunt.loadNpmTasks('grunt-concurrent');
    grunt.loadNpmTasks('grunt-shell');
    grunt.loadNpmTasks('grunt-fest');
    grunt.loadNpmTasks('grunt-sass');

    grunt.registerTask('default', ['concurrent']);

};