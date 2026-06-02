import { Component } from '@angular/core';

@Component({
    standalone: true,
    selector: 'app-footer',
    template: `<div class="layout-footer">
        Miria Geek Store desenvolvido por
        <a href="https://github.com/juliocesarcoutinhodev" target="_blank" rel="noopener noreferrer" class="text-primary font-bold hover:underline">Julio Cesar Coutinho</a>
    </div>`
})
export class AppFooter {}
